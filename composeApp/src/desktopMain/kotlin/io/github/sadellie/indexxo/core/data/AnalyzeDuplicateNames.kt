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

import io.github.sadellie.indexxo.core.model.DuplicateName
import io.github.sadellie.indexxo.core.model.DuplicateNamesAnalyzing
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObject
import io.github.sadellie.indexxo.core.model.IndexingStage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun analyzeDuplicateFileNames(
  indexedObjects: List<IndexedObject>,
  callback: suspend (IndexingStage) -> Unit,
): List<DuplicateName> =
  withContext(Dispatchers.Default) {
    return@withContext analyzeDuplicateNames(
      indexedObjects = indexedObjects.filter { it.fileCategory != FileCategory.FOLDER },
      callback = callback,
    )
  }

suspend fun analyzeDuplicateFolderNames(
  indexedObjects: List<IndexedObject>,
  callback: suspend (IndexingStage) -> Unit,
): List<DuplicateName> =
  withContext(Dispatchers.Default) {
    return@withContext analyzeDuplicateNames(
      indexedObjects = indexedObjects.filter { it.fileCategory == FileCategory.FOLDER },
      callback = callback,
    )
  }

private suspend fun analyzeDuplicateNames(
  indexedObjects: List<IndexedObject>,
  callback: suspend (IndexingStage) -> Unit,
): List<DuplicateName> {
  var index = 0f

  return indexedObjects
    .groupBy { indexedObject ->
      callback(DuplicateNamesAnalyzing(index / indexedObjects.size, indexedObject))
      index++
      indexedObject.path.name.lowercase()
    }
    .filterValues { it.size > 1 }
    .map { (name, duplicates) ->
      DuplicateName(
        duplicates = duplicates.sortedBy { it.createdDate },
        totalSizeBytes = duplicates.sumOf { it.sizeBytes },
        name = name,
      )
    }
    .sortedByDescending { it.duplicates.size }
}
