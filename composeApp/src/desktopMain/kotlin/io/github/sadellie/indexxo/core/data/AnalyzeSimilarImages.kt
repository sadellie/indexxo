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
import io.github.sadellie.indexxo.core.common.mapAsyncAwaitAll
import io.github.sadellie.indexxo.core.model.ComputingHash
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObject
import io.github.sadellie.indexxo.core.model.IndexingStage
import io.github.sadellie.indexxo.core.model.SimilarImagesComparing
import io.github.sadellie.indexxo.core.model.SimilarIndexedObjectsGroup
import io.github.sadellie.indexxo.core.model.Warning
import io.github.sadellie.indexxo.core.model.cleanUp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withContext
import pdqhashing.hasher.PDQHasher
import pdqhashing.types.Hash256
import java.awt.image.BufferedImage
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

suspend fun analyzeSimilarImages(
  indexedObjects: List<IndexedObject>,
  minSimilarity: Float,
  compareColors: Boolean,
  maxThreads: Int,
  callback: suspend (IndexingStage) -> Unit,
): Pair<List<SimilarIndexedObjectsGroup>, List<Warning>> = withContext(Dispatchers.Default) {
  Logger.d(TAG) { "Looking for similar images" }

  val images = indexedObjects
    .filter { it.fileCategory == FileCategory.IMAGE }
    .sortedBy { it.createdDate }
  val describedImagesWithWarnings = describeImages(images, compareColors, maxThreads, callback)
  val descriptedImages = describedImagesWithWarnings
    .filterIsInstance<Pair<IndexedObject, ImageDescriptor>>()
    .toMap()
  val indexedWarnings = describedImagesWithWarnings
    .filterIsInstance<Warning>()

  val imagesWithDuplicates = mutableMapOf<IndexedObject, MutableList<IndexedObject>>()
  var currentIndex = 0f
  for ((baseImage, baseDescriptor) in descriptedImages) {
    val progress = ++currentIndex / descriptedImages.size
    callback(SimilarImagesComparing(progress, baseImage))
    Logger.d(TAG) { "Looking for similar images of ${baseImage.path}" }

    val (mostSimilarImage, _) = findFirstSimilarImage(
      baseImage = baseImage,
      baseImageDescriptor = baseDescriptor,
      imageDescriptors = descriptedImages,
      compareColors = compareColors,
      minSimilarity = minSimilarity,
    ) ?: continue

    val duplicates = imagesWithDuplicates.getOrPut(mostSimilarImage) { mutableListOf() }
    duplicates.add(baseImage)
  }

  val sortedSimilarGroups = imagesWithDuplicates
    .cleanUp()
    .map { (base, duplicates) ->
      val list = duplicates + base
      SimilarIndexedObjectsGroup(list.sortedBy { it.path }, list.sumOf { it.sizeBytes })
    }

  return@withContext sortedSimilarGroups to indexedWarnings
}

private suspend fun CoroutineScope.describeImages(
  images: List<IndexedObject>,
  compareColors: Boolean,
  maxThreads: Int,
  callback: suspend (IndexingStage) -> Unit
): List<Any> {
  val pdqHasher = PDQHasher()
  val atomicCounter = AtomicInteger()
  return images.mapAsyncAwaitAll(this, Semaphore(maxThreads)) { image ->
    val progress = atomicCounter.incrementAndGet().toFloat() / images.size
    callback(ComputingHash(progress, image))
    val bufferedImage = try {
      withContext(Dispatchers.IO) {
        ImageIO.read(image.path.toFile())
      }
    } catch (e: IOException) {
      Logger.w(e, TAG) { "$image can't be decoded" }
      null
    }

    if (bufferedImage == null) {
      Logger.w(TAG) { "$image is null" }
      return@mapAsyncAwaitAll Warning(image.path, "Decoder error", null)
    }

    val hashes = pdqHashes(bufferedImage, pdqHasher)
    val histogram = if (compareColors) histogram(bufferedImage) else null
    val imageDescriptor = ImageDescriptor(hashes, histogram)
    return@mapAsyncAwaitAll image to imageDescriptor
  }
}

private fun findFirstSimilarImage(
  baseImage: IndexedObject,
  baseImageDescriptor: ImageDescriptor,
  imageDescriptors: Map<IndexedObject, ImageDescriptor>,
  compareColors: Boolean,
  minSimilarity: Float,
): Pair<IndexedObject, ImageSimilarity>? {
  for ((testImage, testImageDescriptor) in imageDescriptors) {
    // Don't self check
    if (testImage.path == baseImage.path) continue
    val hashSimilarity = crossCheckHashes(baseImageDescriptor.hashes, testImageDescriptor.hashes)
    val colorSimilarity: Float = if (compareColors) {
      if (baseImageDescriptor.histogram != null && testImageDescriptor.histogram != null) {
        compareHistograms(baseImageDescriptor.histogram, testImageDescriptor.histogram)
      } else {
        0f
      }
    } else {
      0f
    }
    val adjustedColorSimilarity =
      colorSimilarity.pow(COLOR_SIMILARITY_POWER) * COLOR_SIMILARITY_WEIGHT
    val totalSimilarity = (hashSimilarity + adjustedColorSimilarity).coerceAtMost(1f)
    if (totalSimilarity >= minSimilarity) {
      return testImage to ImageSimilarity(totalSimilarity, hashSimilarity, colorSimilarity)
    }
  }

  return null
}

private fun pdqHashes(
  image: BufferedImage,
  pdqHasher: PDQHasher
): Set<Hash256> {
  val hashesAndQuality = pdqHasher.dihedralFromBufferedImage(image)
  return setOf(
    hashesAndQuality.hash,
    hashesAndQuality.hashRotate90,
    hashesAndQuality.hashRotate180,
    hashesAndQuality.hashRotate270,
    hashesAndQuality.hashFlipX,
    hashesAndQuality.hashFlipY,
    hashesAndQuality.hashFlipPlus1,
    hashesAndQuality.hashFlipMinus1,
  )
}

private fun histogram(
  image: BufferedImage
): IntArray {
  val histogram = IntArray(HISTOGRAM_LENGTH)
  for (row in 0 until image.width) {
    for (col in 0 until image.height) {
      val rgb = image.getRGB(row, col)
      val red = (rgb shr RED_OFFSET) and HEX_MASK
      val green = (rgb shr GREEN_OFFSET) and HEX_MASK
      val blue = rgb and HEX_MASK
      val luminance =
        (RED_LUMINANCE * red + GREEN_LUMINANCE * green + BLUE_LUMINANCE * blue).toInt()
      histogram[luminance]++
    }
  }

  return histogram.normalize()
}

private fun IntArray.normalize(): IntArray {
  val oldMax = this.max()
  val newMax = HISTOGRAM_NORM_MAX
  val factor = newMax / oldMax

  this.indices.forEach { i ->
    val j = this[i]
    val norm = j * factor
    this[i] = norm.roundToInt()
  }

  return this
}

private fun crossCheckHashes(
  baseHashes: Iterable<Hash256>,
  testHashes: Iterable<Hash256>,
): Float {
  var maxConfidence = 0f

  for (baseHash in baseHashes) {
    for (testHash in testHashes) {
      // 0..1
      val confidenceScore = baseHash.distanceNormalized(testHash)
      if (confidenceScore == 1f) return 1f
      if (confidenceScore > maxConfidence) {
        maxConfidence = confidenceScore
      }
    }
  }

  return maxConfidence
}

private fun compareHistograms(hist1: IntArray, hist2: IntArray): Float {
  val n = hist1.size
  var s1 = 0f
  var s2 = 0f
  var s11 = 0f
  var s22 = 0f
  var s12 = 0f
  hist1.indices.forEach { i ->
    val v1 = hist1[i]
    val v2 = hist2[i]

    s1 += v1
    s2 += v2
    s11 += v1 * v1
    s22 += v2 * v2
    s12 += v1 * v2
  }
  val nominator = s12 - (s1 * s2 / n)
  val denominator = sqrt((s11 - (s1.pow(2) / n)) * (s22 - (s2.pow(2) / n)))

  return if (denominator == 0f) {
    0f
  } else {
    // Normalized result. Was in range [-1..1], now in range [0..1]
    ((nominator / denominator) + 1) / 2
  }
}

private data class ImageDescriptor(
  val hashes: Set<Hash256>,
  val histogram: IntArray?
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as ImageDescriptor

    if (hashes != other.hashes) return false
    if (histogram != null) {
      if (other.histogram == null) return false
      if (!histogram.contentEquals(other.histogram)) return false
    } else if (other.histogram != null) return false

    return true
  }

  override fun hashCode(): Int {
    var result = hashes.hashCode()
    result = 31 * result + (histogram?.contentHashCode() ?: 0)
    return result
  }
}

private data class ImageSimilarity(
  val totalSimilarity: Float,
  val featureSimilarity: Float,
  val colorSimilarity: Float,
)

// Higher values increase the tolerance for nonsense when comparing colors, for example a similarity
// of 0.9 does not necessarily mean the images are 90% similar.
private const val COLOR_SIMILARITY_POWER = 12
private const val COLOR_SIMILARITY_WEIGHT = 0.3f
private const val HISTOGRAM_LENGTH = 256
private const val RED_OFFSET = 16
private const val GREEN_OFFSET = 8
private const val HEX_MASK = 0xFF
private const val RED_LUMINANCE = 0.2126
private const val GREEN_LUMINANCE = 0.7152
private const val BLUE_LUMINANCE = 0.0722
private const val HISTOGRAM_NORM_MAX = 255f

private const val TAG = "AnalyzeSimilarImages"
