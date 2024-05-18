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

import io.github.sadellie.indexxo.core.model.EmptyFilesAnalyzing
import io.github.sadellie.indexxo.core.model.EmptyFoldersAnalyzing
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObject
import io.github.sadellie.indexxo.core.model.IndexingStage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun analyzeEmptyFiles(
  indexedObjects: List<IndexedObject>,
  callback: suspend (IndexingStage) -> Unit
): List<IndexedObject> = withContext(Dispatchers.Default) {
  return@withContext indexedObjects
    .filter { it.fileCategory != FileCategory.FOLDER }
    .filterIndexed { index, indexedObject ->
      callback(EmptyFilesAnalyzing(index.toFloat() / indexedObjects.size, indexedObject))
      indexedObject.sizeBytes <= 0L
    }
}

suspend fun analyzeEmptyFolders(
  indexedObjects: List<IndexedObject>,
  callback: suspend (IndexingStage) -> Unit,
): List<IndexedObject> = withContext(Dispatchers.Default) {
  val foldersWithChildren = indexedObjects.mapNotNull { it.parentPath }.distinct()
  val emptyFolders =
    indexedObjects.filterIndexed { index, indexedObject ->
      if (indexedObject.fileCategory != FileCategory.FOLDER) return@filterIndexed false
      callback(EmptyFoldersAnalyzing(index.toFloat() / indexedObjects.size, indexedObject))

      return@filterIndexed indexedObject.path !in foldersWithChildren
    }

  return@withContext emptyFolders
}
