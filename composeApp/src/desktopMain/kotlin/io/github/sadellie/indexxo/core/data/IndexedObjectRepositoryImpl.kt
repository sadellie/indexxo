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
import io.github.sadellie.indexxo.core.common.SAMPLE_SIZE
import io.github.sadellie.indexxo.core.common.indexedObjectJson
import io.github.sadellie.indexxo.core.database.model.UserPreset
import io.github.sadellie.indexxo.core.model.DuplicateHash
import io.github.sadellie.indexxo.core.model.DuplicateName
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObject
import io.github.sadellie.indexxo.core.model.LocalDateTimeRange
import io.github.sadellie.indexxo.core.model.SimilarIndexedObjectsGroup
import io.github.sadellie.indexxo.core.model.SortType
import io.github.sadellie.indexxo.core.model.Warning
import io.github.sadellie.indexxo.core.model.moveToTrash
import io.github.sadellie.indexxo.core.model.remove
import io.github.sadellie.indexxo.core.model.retain
import io.github.sadellie.indexxo.core.model.search
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import okio.Path

class IndexedObjectRepositoryImpl : IndexedObjectRepository {
  override val warnings = MutableStateFlow(emptyList<Warning>())
  override val indexedObjects = MutableStateFlow(emptyList<IndexedObject>())

  override val duplicateHashes = MutableStateFlow(emptyList<DuplicateHash>())
  override val duplicateFileNames = MutableStateFlow(emptyList<DuplicateName>())
  override val duplicateFolderNames = MutableStateFlow(emptyList<DuplicateName>())
  override val emptyFiles = MutableStateFlow(emptyList<IndexedObject>())
  override val emptyFolders = MutableStateFlow(emptyList<IndexedObject>())
  override val similarImages = MutableStateFlow(emptyList<SimilarIndexedObjectsGroup>())
  override val similarVideos = MutableStateFlow(emptyList<SimilarIndexedObjectsGroup>())

  override fun index(userPreset: UserPreset) = channelFlow {
    indexedObjects.update { emptyList() }
    syncIndexes()

    val (indexed, warningIndexed) = indexWithWarnings(
      includedPaths = (userPreset.includedDirectories + userPreset.includedFiles).map { it.path },
      excludedPaths = (userPreset.excludedDirectories + userPreset.excludedFiles).map { it.path },
      includedExtensions = userPreset.includedExtensions.map { it.extension },
      excludedExtensions = userPreset.excludedExtensions.map { it.extension },
      maxThreads = userPreset.maxThreads,
      callback = ::trySend,
    )

    indexedObjects.update { indexed }
    warnings.update { it + warningIndexed }

    if (userPreset.isDuplicateHashesEnabled) {
      val duplicates = analyzeDuplicateHashes(indexedObjects.value, SAMPLE_SIZE, ::trySend)
      duplicateHashes.update { duplicates.first }
      warnings.update { it + duplicates.second }
    }

    if (userPreset.isDuplicateFileNamesEnabled) {
      val duplicates = analyzeDuplicateFileNames(indexedObjects.value, ::trySend)
      duplicateFileNames.update { duplicates }
    }

    if (userPreset.isDuplicateFolderNamesEnabled) {
      val duplicates = analyzeDuplicateFolderNames(indexedObjects.value, ::trySend)
      duplicateFolderNames.update { duplicates }
    }

    if (userPreset.isEmptyFilesEnabled) {
      val empty = analyzeEmptyFiles(indexedObjects.value, ::trySend)
      emptyFiles.update { empty }
    }

    if (userPreset.isEmptyFoldersEnabled) {
      val empty = analyzeEmptyFolders(indexedObjects.value, ::trySend)
      emptyFolders.update { empty }
    }

    if (userPreset.isSimilarImagesEnabled) {
      val duplicates = analyzeSimilarImages(
        indexedObjects = indexed,
        minSimilarity = userPreset.similarImagesMinSimilarity,
        compareColors = userPreset.isSimilarImagesImproveAccuracy,
        maxThreads = userPreset.maxThreads,
        callback = ::trySend,
      )
      similarImages.update { duplicates.first }
      warnings.update { it + duplicates.second }
    }

    if (userPreset.isSimilarVideosEnabled) {
      val duplicates = analyzeSimilarVideos(
        indexedObjects = indexed,
        minHashSimilarity = userPreset.similarVideosMinimalHashSimilarity,
        minFrameSimilarity = userPreset.similarVideosMinimalFrameSimilarity,
        framePerSecond = userPreset.similarVideosFPS,
        callback = ::trySend,
      )
      similarVideos.update { duplicates }
    }
  }

  override suspend fun moveToTrash(paths: Set<Path>, callback: (Path) -> Unit) {
    paths.forEach { path ->
      callback(path)
      indexedObjects.update { it.moveToTrash(path) }
    }
  }

  override suspend fun discardDuplicateHashes(paths: Set<Path>) =
    duplicateHashes.update { it.remove(paths) }

  override suspend fun discardDuplicateFileNames(paths: Set<Path>) =
    duplicateFileNames.update { it.remove(paths) }

  override suspend fun discardDuplicateFolderNames(paths: Set<Path>) =
    duplicateFolderNames.update { it.remove(paths) }

  override suspend fun discardEmptyFiles(paths: Set<Path>) =
    emptyFiles.update { files -> files.filter { it.path !in paths } }

  override suspend fun discardEmptyFolders(paths: Set<Path>) =
    emptyFolders.update { folders -> folders.filter { it.path !in paths } }

  override suspend fun discardSimilarImages(paths: Set<Path>) =
    similarImages.update { it.remove(paths) }

  override suspend fun discardSimilarVideos(paths: Set<Path>) =
    similarImages.update { it.remove(paths) }

  override suspend fun discardWarning(warning: Warning) {
    warnings.update { it - warning }
  }

  override suspend fun search(
    textQuery: String,
    fileCategories: List<FileCategory>,
    createdDateRange: LocalDateTimeRange?,
    modifiedDateRange: LocalDateTimeRange?,
    includeContents: Boolean,
    sortType: SortType,
    sortDescending: Boolean,
    maxThreads: Int,
  ): List<IndexedObject> = withContext(Dispatchers.Default) {
    return@withContext indexedObjects.first().search(
      textQuery = textQuery,
      fileCategories = fileCategories,
      createdDateRange = createdDateRange,
      modifiedDateRange = modifiedDateRange,
      includeContents = includeContents,
      sortType = sortType,
      sortDescending = sortDescending,
      maxThreads = maxThreads,
    )
  }

  override suspend fun export(path: Path) = withContext(Dispatchers.IO) {
    Logger.d { "Exporting to $path" }
    val encodedIndex = indexedObjectJson.encodeToString(indexedObjects.first())
    path.toFile().bufferedWriter().use { it.write(encodedIndex) }
  }

  override suspend fun syncIndexes() {
    val indexPaths = indexedObjects.value.map { it.path }.toSet()

    duplicateHashes.update { it.retain(indexPaths) }
    duplicateFileNames.update { it.retain(indexPaths) }
    duplicateFolderNames.update { it.retain(indexPaths) }
    emptyFiles.update { files -> files.filter { it.path in indexPaths } }
    emptyFolders.update { folders -> folders.filter { it.path in indexPaths } }
    similarImages.update { it.retain(indexPaths) }
    similarVideos.update { it.retain(indexPaths) }
  }
}
