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

import io.github.sadellie.indexxo.core.database.model.UserPreset
import io.github.sadellie.indexxo.core.model.DuplicateHash
import io.github.sadellie.indexxo.core.model.DuplicateName
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObject
import io.github.sadellie.indexxo.core.model.IndexingStage
import io.github.sadellie.indexxo.core.model.LocalDateTimeRange
import io.github.sadellie.indexxo.core.model.SimilarIndexedObjectsGroup
import io.github.sadellie.indexxo.core.model.SortType
import io.github.sadellie.indexxo.core.model.Warning
import kotlinx.coroutines.flow.Flow
import okio.Path

interface IndexedObjectRepository {
  val warnings: Flow<List<Warning>>
  val indexedObjects: Flow<List<IndexedObject>>
  val duplicateHashes: Flow<List<DuplicateHash>>
  val duplicateFileNames: Flow<List<DuplicateName>>
  val duplicateFolderNames: Flow<List<DuplicateName>>
  val emptyFiles: Flow<List<IndexedObject>>
  val emptyFolders: Flow<List<IndexedObject>>
  val similarImages: Flow<List<SimilarIndexedObjectsGroup>>
  val similarVideos: Flow<List<SimilarIndexedObjectsGroup>>

  fun index(userPreset: UserPreset): Flow<IndexingStage>
  suspend fun moveToTrash(paths: Set<Path>, callback: (Path) -> Unit)
  suspend fun discardDuplicateHashes(paths: Set<Path>)
  suspend fun discardDuplicateFileNames(paths: Set<Path>)
  suspend fun discardDuplicateFolderNames(paths: Set<Path>)
  suspend fun discardEmptyFiles(paths: Set<Path>)
  suspend fun discardEmptyFolders(paths: Set<Path>)
  suspend fun discardSimilarImages(paths: Set<Path>)
  suspend fun discardSimilarVideos(paths: Set<Path>)
  suspend fun discardWarning(warning: Warning)
  suspend fun search(
    textQuery: String,
    fileCategories: List<FileCategory>,
    createdDateRange: LocalDateTimeRange?,
    modifiedDateRange: LocalDateTimeRange?,
    includeContents: Boolean,
    sortType: SortType,
    sortDescending: Boolean,
    maxThreads: Int,
  ): List<IndexedObject>

  suspend fun export(path: Path)
  suspend fun syncIndexes()
}
