/*
 * Indexxo is file management software.
 * Copyright (c) 2024 Elshan Agaev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.sadellie.indexxo.core.data

import co.touchlab.kermit.Logger
import io.github.sadellie.indexxo.core.model.ComputingHash
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObject
import io.github.sadellie.indexxo.core.model.IndexingStage
import io.github.sadellie.indexxo.core.model.SimilarIndexedObjectsGroup
import io.github.sadellie.indexxo.core.model.SimilarVideosComparing
import io.github.sadellie.indexxo.core.model.cleanUp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bytedeco.javacv.Java2DFrameConverter
import pdqhashing.hasher.PDQHasher
import pdqhashing.types.Hash256
import pdqhashing.types.HashesAndQuality

suspend fun analyzeSimilarVideos(
  indexedObjects: List<IndexedObject>,
  minHashSimilarity: Float,
  minFrameSimilarity: Float,
  framePerSecond: Int,
  maxThreads: Int,
  callback: suspend (IndexingStage) -> Unit,
): List<SimilarIndexedObjectsGroup> = withContext(Dispatchers.Default) {
  Logger.d(TAG) { "Looking for similar videos" }

  val videos = indexedObjects
    .filter { it.fileCategory == FileCategory.VIDEO }
    .sortedBy { it.createdDate }
  val descriptedVideos = describeVideos(videos, framePerSecond, maxThreads, callback)
    .filterNotNull()
    .toMap()

  val videosWithDuplicates = mutableMapOf<IndexedObject, MutableList<IndexedObject>>()
  var currentIndex = 0f
  for ((baseVideo, baseFrames) in descriptedVideos) {
    val progress = ++currentIndex / indexedObjects.size
    callback(SimilarVideosComparing(progress, baseVideo))
    Logger.d(TAG) { "Looking for similar videos of ${baseVideo.path}" }

    val (mostSimilarVideo, _) = findMostSimilarVideo(
      baseVideo,
      baseFrames,
      descriptedVideos,
      minHashSimilarity,
      minFrameSimilarity,
    ) ?: continue

    val duplicates = videosWithDuplicates.getOrPut(mostSimilarVideo) { mutableListOf() }
    duplicates.add(baseVideo)
  }

  val similarVideoGroups = videosWithDuplicates
    .cleanUp()
    .map { (base, duplicates) ->
      val list = duplicates + base
      SimilarIndexedObjectsGroup(list.sortedBy { it.path }, list.sumOf { it.sizeBytes })
    }

  return@withContext similarVideoGroups
}

private suspend fun describeVideos(
  videos: List<IndexedObject>,
  framePerSecond: Int,
  maxThreads: Int,
  callback: suspend (IndexingStage) -> Unit,
): List<Pair<IndexedObject, Set<Hash256>>?> {
  val converter = Java2DFrameConverter()
  val pdqHasher = PDQHasher()
  return videos.mapIndexed { index, video ->
    callback(ComputingHash(index.toFloat() / videos.size, video))

    val frames = try {
      uniqueFrames(video, converter, pdqHasher, framePerSecond, maxThreads)
    } catch (e: Exception) {
      Logger.e(TAG, e) { "Failed to get unique frames" }
      return@mapIndexed null
    }

    video to frames
  }
}

private fun findMostSimilarVideo(
  baseVideo: IndexedObject,
  baseFrames: Set<Hash256>,
  mapOfFrames: Map<IndexedObject, Set<Hash256>>,
  minHashSimilarity: Float,
  minFrameSimilarity: Float
): Pair<IndexedObject, Float>? {
  for ((testVideo, testFrames) in mapOfFrames) {
    // no self check
    if (testVideo.path == baseVideo.path) continue
    val totalSimilarity = frameSetsSimilarity(baseFrames, testFrames, minHashSimilarity)
    if (totalSimilarity >= minFrameSimilarity) {
      return testVideo to totalSimilarity
    }
  }
  return null
}

/**
 * @param baseFrames
 * @param testFrames
 * @param minHashSimilarity How similar frames should be to consider them same. From 0 to 1
 */
private fun frameSetsSimilarity(
  baseFrames: Set<Hash256>,
  testFrames: Set<Hash256>,
  minHashSimilarity: Float,
): Float {
  // how many frames from test are in base (how much test stole from base)
  var testInBaseMatches = 0
  for (testFrame in testFrames) {
    for (baseFrame in baseFrames) {
      val distance = baseFrame.distanceNormalized(testFrame)
      if (distance > minHashSimilarity) {
        testInBaseMatches++
        break
      }
    }
  }

  val testInBaseMatchedPercents: Float = testInBaseMatches.toFloat() / testFrames.size

  return testInBaseMatchedPercents
}

internal suspend fun uniqueFrames(
  video: IndexedObject,
  converter: Java2DFrameConverter,
  pdqHasher: PDQHasher,
  framePerSecond: Int,
  maxThreads: Int,
): Set<Hash256> = withContext(Dispatchers.IO) {
  val uniqueFrames = mutableListOf<HashesAndQuality>()
  FFMpegFrameGrabber2(video.path.toFile(), maxThreads).use { grabber ->
    grabber.processFrames(framePerSecond.toDouble()) { frame ->
      val image = converter.getBufferedImage(frame)
      val currentFrameHashes = pdqHasher.dihedralFromBufferedImage(image)
      val lastFrameHashes = uniqueFrames.lastOrNull()
      if (lastFrameHashes != null) {
        // Pruning
        // don't insert anything from frame if hash is too similar to last frame hash
        if (areFrameHashesSimilar(currentFrameHashes, lastFrameHashes)) {
          return@processFrames
        }
      }

      uniqueFrames.add(currentFrameHashes)
    }
  }

  return@withContext uniqueFrames
    .flatMap {
      setOf(
        it.hash, it.hashRotate90, it.hashRotate180, it.hashRotate270,
        it.hashFlipX, it.hashFlipY, it.hashFlipPlus1, it.hashFlipMinus1,
      )
    }
    .toSet()
}

private fun areFrameHashesSimilar(
  hashA: HashesAndQuality,
  hashB: HashesAndQuality,
): Boolean {
  if (hashA.hash.hammingDistance(hashB.hash) <= PRUNING_THRESHOLD) return true
  if (hashA.hashRotate90.hammingDistance(hashB.hash) <= PRUNING_THRESHOLD) return true
  if (hashA.hashRotate180.hammingDistance(hashB.hash) <= PRUNING_THRESHOLD) return true
  if (hashA.hashRotate270.hammingDistance(hashB.hash) <= PRUNING_THRESHOLD) return true
  if (hashA.hashFlipX.hammingDistance(hashB.hash) <= PRUNING_THRESHOLD) return true
  if (hashA.hashFlipY.hammingDistance(hashB.hash) <= PRUNING_THRESHOLD) return true
  if (hashA.hashFlipPlus1.hammingDistance(hashB.hash) <= PRUNING_THRESHOLD) return true
  if (hashA.hashFlipMinus1.hammingDistance(hashB.hash) <= PRUNING_THRESHOLD) return true
  return false
}

private const val PRUNING_THRESHOLD = 2
private const val TAG = "AnalyzeSimilarVideos"
