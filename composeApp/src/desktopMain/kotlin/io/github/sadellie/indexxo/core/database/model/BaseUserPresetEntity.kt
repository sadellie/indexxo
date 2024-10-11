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

package io.github.sadellie.indexxo.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.sadellie.indexxo.core.common.maxSystemThreads

@Entity(tableName = "base_user_preset")
data class BaseUserPresetEntity(
  @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
  @ColumnInfo(name = "name") val name: String,
  @ColumnInfo(name = "maxThreads") val maxThreads: Int = maxSystemThreads,
  @ColumnInfo(name = "isDuplicateHashesEnabled") val isDuplicateHashesEnabled: Boolean = true,
  @ColumnInfo(name = "isDuplicateFileNamesEnabled") val isDuplicateFileNamesEnabled: Boolean = true,
  @ColumnInfo(name = "isDuplicateFolderNamesEnabled") val isDuplicateFolderNamesEnabled: Boolean = true,
  @ColumnInfo(name = "isEmptyFoldersEnabled") val isEmptyFoldersEnabled: Boolean = true,
  @ColumnInfo(name = "isEmptyFilesEnabled") val isEmptyFilesEnabled: Boolean = true,
  @ColumnInfo(name = "isSimilarImagesEnabled") val isSimilarImagesEnabled: Boolean = true,
  @ColumnInfo(name = "similarImagesMinSimilarity") val similarImageMinSimilarity: Float = 0.9f,
  @ColumnInfo(name = "isSimilarImagesImproveAccuracy") val isSimilarImagesImproveAccuracy: Boolean = true,
  @ColumnInfo(name = "isSimilarVideosEnabled") val isSimilarVideosEnabled: Boolean = true,
  @ColumnInfo(name = "similarVideosMinimalHashSimilarity") val similarVideosMinimalHashSimilarity: Float = 0.7f,
  @ColumnInfo(name = "similarVideosMinimalFrameSimilarity") val similarVideosMinimalFrameSimilarity: Float = 0.7f,
  @ColumnInfo(name = "similarVideosFPS") val similarVideosFPS: Int = 5,
)
