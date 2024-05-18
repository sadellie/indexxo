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
import io.github.sadellie.indexxo.core.common.MAX_BUFFER_SIZE
import io.github.sadellie.indexxo.core.common.calculateFullHash
import io.github.sadellie.indexxo.core.common.calculatePartialHash
import io.github.sadellie.indexxo.core.model.DuplicateHash
import io.github.sadellie.indexxo.core.model.DuplicateHashesAnalyzing
import io.github.sadellie.indexxo.core.model.DuplicateHashesComputingFullHash
import io.github.sadellie.indexxo.core.model.IndexedObject
import io.github.sadellie.indexxo.core.model.IndexingStage
import io.github.sadellie.indexxo.core.model.Warning
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

suspend fun analyzeDuplicateHashes(
  indexedObjects: List<IndexedObject>,
  partialHashSampleSizeBytes: Int,
  callback: suspend (IndexingStage) -> Unit
): Pair<List<DuplicateHash>, List<Warning>> = withContext(Dispatchers.Default) {
  val indexedWarnings = mutableListOf<Warning>()

  val indexedObjectsWithSameFileSize = indexedObjects
    .groupBy { it.sizeBytes }
    .filter { (sizeBytes, indexedObjectsWithSameSizes) ->
      sizeBytes != 0L && indexedObjectsWithSameSizes.size > 1
    }
    .values
    .flatten()

  var loopIndex = 0f
  val indexedObjectsWithSamePartialHash = mutableMapOf<Long, MutableSet<IndexedObject>>()
  for (indexedObject in indexedObjectsWithSameFileSize) {
    val progress = loopIndex++ / indexedObjectsWithSameFileSize.size
    callback(DuplicateHashesAnalyzing(progress, indexedObject))

    try {
      Logger.v(TAG) { "Calculate partial hash for: ${indexedObject.path}" }
      val partialHash = indexedObject.path.toFile().calculatePartialHash(partialHashSampleSizeBytes)
      if (partialHash == 0L) continue

      val list = indexedObjectsWithSamePartialHash.getOrPut(partialHash) { mutableSetOf() }
      list.add(indexedObject)
    } catch (e: IOException) {
      Logger.w(TAG, e) { "Failed to calculate partial hash for: ${indexedObject.path}" }
      indexedWarnings.add(Warning(indexedObject.path, e.message, e.stackTraceToString()))
      continue
    }
  }

  loopIndex = 0f

  val indexedObjectsWithSamePartialHashFiltered = indexedObjectsWithSamePartialHash
    .filter { it.value.size > 1 }
    .flatMap { it.value }

  val indexedObjectsWithSameFullHash = mutableMapOf<Long, MutableSet<IndexedObject>>()
  for (indexedObject in indexedObjectsWithSamePartialHashFiltered) {
    val progress = loopIndex++ / indexedObjectsWithSamePartialHash.size
    callback(DuplicateHashesComputingFullHash(progress, indexedObject))

    try {
      Logger.v(TAG) { "Calculate full hash for: ${indexedObject.path}" }
      val fullHash = indexedObject.path.toFile().calculateFullHash(MAX_BUFFER_SIZE)
      val list = indexedObjectsWithSameFullHash.getOrPut(fullHash) { mutableSetOf() }
      list.add(indexedObject)
    } catch (e: IOException) {
      Logger.w(TAG, e) { "Failed to calculate full hash for: ${indexedObject.path}" }
      indexedWarnings.add(Warning(indexedObject.path, e.message, e.stackTraceToString()))
      continue
    }
  }

  val duplicates = indexedObjectsWithSameFullHash
    .filterValues { it.size > 1 }
    .map { (hash, duplicates) ->
      DuplicateHash(
        duplicates = duplicates.sortedBy { it.createdDate },
        totalSizeBytes = duplicates.sumOf { it.sizeBytes },
        hash = hash,
      )
    }
    .sortedByDescending { it.duplicates.size }

  return@withContext duplicates to indexedWarnings
}

private const val TAG = "AnalyzeDuplicateHashes"
