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

import io.github.sadellie.indexxo.core.database.IndexxoDatabaseDao
import io.github.sadellie.indexxo.core.database.model.BaseUserPresetEntity
import io.github.sadellie.indexxo.core.database.model.UserPreset
import io.github.sadellie.indexxo.core.database.model.UserPresetEntity
import io.github.sadellie.indexxo.core.database.model.UserPresetExtensionEntity
import io.github.sadellie.indexxo.core.database.model.UserPresetPathEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import okio.Path

class UserPresetsRepositoryImpl(
  private val dao: IndexxoDatabaseDao,
  preferencesRepository: PreferencesRepository
) : UserPresetsRepository {
  @OptIn(ExperimentalCoroutinesApi::class)
  override val allUserPresets: Flow<List<UserPreset>> =
    dao
      .getUserPresets()
      .mapLatest { presets -> presets.map(UserPresetEntity::toUserPreset) }
      .flowOn(Dispatchers.IO)

  @OptIn(ExperimentalCoroutinesApi::class)
  override val currentUserPreset: Flow<UserPreset?> = preferencesRepository
    .indexxoPreferencesFlow
    .flatMapLatest { userPreset ->
      dao.getUserPreset(userPreset.presetId).mapLatest { it?.toUserPreset() }
    }
    .flowOn(Dispatchers.IO)

  /**
   * Will create a preset with [name] and return [BaseUserPresetEntity.id] of the created record.
   */
  override suspend fun createUserPreset(name: String): Int =
    withContext(Dispatchers.IO) {
      val baseUserPresetEntity = BaseUserPresetEntity(name = name)
      val created = dao.insertBaseUserPreset(baseUserPresetEntity)

      return@withContext created.toInt()
    }

  override suspend fun updateUserPreset(userPreset: UserPreset) =
    withContext(Dispatchers.IO) {
      val basePresetEntity = userPreset.toBaseUserPresetEntity()
      dao.updateBaseUserPreset(basePresetEntity)
    }

  override suspend fun deleteUserPreset(userPreset: UserPreset) =
    withContext(Dispatchers.IO) {
      val basePresetEntity = userPreset.toBaseUserPresetEntity()
      dao.deleteBaseUserPreset(basePresetEntity)
    }

  override suspend fun insertUserPresetPath(
    path: Path,
    isIncluded: Boolean,
    isDirectory: Boolean,
    basePresetId: Int
  ) = withContext(Dispatchers.IO) {
    dao.insertUserPresetPath(
      UserPresetPathEntity(
        path = path,
        isIncluded = isIncluded,
        isDirectory = isDirectory,
        basePresetId = basePresetId,
      ),
    )
  }

  override suspend fun updateUserPresetPath(id: Int, path: Path) =
    withContext(Dispatchers.IO) { dao.updateUserPresetPath(id = id, path = path) }

  override suspend fun deleteUserPresetPath(id: Int) =
    withContext(Dispatchers.IO) { dao.deleteUserPresetPath(id) }

  override suspend fun addExtension(extension: String, included: Boolean, basePresetId: Int) =
    withContext(Dispatchers.IO) {
      dao.insertUserPresetExtension(
        UserPresetExtensionEntity(
          extension = extension,
          basePresetId = basePresetId,
          isIncluded = included,
        ),
      )
    }

  override suspend fun editExtension(id: Int, extension: String) =
    withContext(Dispatchers.IO) { dao.updateUserPresetExtension(id = id, extension = extension) }

  override suspend fun removeExtension(id: Int) =
    withContext(Dispatchers.IO) { dao.deleteUserPresetExtension(id) }
}

private fun UserPreset.toBaseUserPresetEntity() =
  BaseUserPresetEntity(
    id = id,
    name = name,
    maxThreads = maxThreads,
    isDuplicateHashesEnabled = isDuplicateHashesEnabled,
    isDuplicateFileNamesEnabled = isDuplicateFileNamesEnabled,
    isDuplicateFolderNamesEnabled = isDuplicateFolderNamesEnabled,
    isEmptyFoldersEnabled = isEmptyFoldersEnabled,
    isEmptyFilesEnabled = isEmptyFilesEnabled,
    isSimilarImagesEnabled = isSimilarImagesEnabled,
    similarImageMinSimilarity = similarImagesMinSimilarity,
    isSimilarImagesImproveAccuracy = isSimilarImagesImproveAccuracy,
    isSimilarVideosEnabled = isSimilarVideosEnabled,
    similarVideosMinimalHashSimilarity = similarVideosMinimalHashSimilarity,
    similarVideosMinimalFrameSimilarity = similarVideosMinimalFrameSimilarity,
    similarVideosFPS = similarVideosFPS,
  )

private fun UserPresetEntity.toUserPreset(): UserPreset =
  UserPreset(
    id = baseUserPresetEntity.id,
    name = baseUserPresetEntity.name,
    maxThreads = baseUserPresetEntity.maxThreads,
    includedDirectories = paths.filter { it.isDirectory and it.isIncluded },
    excludedDirectories = paths.filter { it.isDirectory and !it.isIncluded },
    includedFiles = paths.filter { !it.isDirectory and it.isIncluded },
    excludedFiles = paths.filter { !it.isDirectory and !it.isIncluded },
    includedExtensions = extensions.filter { it.isIncluded },
    excludedExtensions = extensions.filter { !it.isIncluded },
    isDuplicateHashesEnabled = baseUserPresetEntity.isDuplicateHashesEnabled,
    isDuplicateFileNamesEnabled = baseUserPresetEntity.isDuplicateFileNamesEnabled,
    isDuplicateFolderNamesEnabled = baseUserPresetEntity.isDuplicateFolderNamesEnabled,
    isEmptyFoldersEnabled = baseUserPresetEntity.isEmptyFoldersEnabled,
    isEmptyFilesEnabled = baseUserPresetEntity.isEmptyFilesEnabled,
    isSimilarImagesEnabled = baseUserPresetEntity.isSimilarImagesEnabled,
    similarImagesMinSimilarity = baseUserPresetEntity.similarImageMinSimilarity,
    isSimilarImagesImproveAccuracy = baseUserPresetEntity.isSimilarImagesImproveAccuracy,
    isSimilarVideosEnabled = baseUserPresetEntity.isSimilarVideosEnabled,
    similarVideosMinimalHashSimilarity = baseUserPresetEntity.similarVideosMinimalHashSimilarity,
    similarVideosMinimalFrameSimilarity = baseUserPresetEntity.similarVideosMinimalFrameSimilarity,
    similarVideosFPS = baseUserPresetEntity.similarVideosFPS,
  )
