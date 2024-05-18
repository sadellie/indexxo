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

package io.github.sadellie.indexxo

import io.github.sadellie.indexxo.core.database.model.UserPreset
import io.github.sadellie.indexxo.core.database.model.UserPresetExtension
import io.github.sadellie.indexxo.core.database.model.UserPresetPath
import okio.Path
import okio.Path.Companion.toPath

val fakeUserPreset = UserPreset(
  id = 0,
  name = "Test",
  maxThreads = 3,
  includedDirectories = listOf(
    object : UserPresetPath {
      override val id: Int = 0
      override val path: Path = "/my_files/documents".toPath()
      override val isIncluded: Boolean = true
      override val isDirectory: Boolean = true
      override val basePresetId: Int = 0
    },
    object : UserPresetPath {
      override val id: Int = 1
      override val path: Path = "/my_files/images".toPath()
      override val isIncluded: Boolean = true
      override val isDirectory: Boolean = true
      override val basePresetId: Int = 0
    },
    object : UserPresetPath {
      override val id: Int = 0
      override val path: Path = "/my_files/work".toPath()
      override val isIncluded: Boolean = true
      override val isDirectory: Boolean = true
      override val basePresetId: Int = 0
    },
  ),
  excludedDirectories = listOf(
    object : UserPresetPath {
      override val id: Int = 0
      override val path: Path = "/my_files/work/drafts".toPath()
      override val isIncluded: Boolean = false
      override val isDirectory: Boolean = true
      override val basePresetId: Int = 0
    },
  ),
  includedFiles = listOf(
    object : UserPresetPath {
      override val id: Int = 4
      override val path: Path = "/dog_names.txt".toPath()
      override val isIncluded: Boolean = true
      override val isDirectory: Boolean = false
      override val basePresetId: Int = 0
    },
    object : UserPresetPath {
      override val id: Int = 5
      override val path: Path = "/cat_names.txt".toPath()
      override val isIncluded: Boolean = true
      override val isDirectory: Boolean = false
      override val basePresetId: Int = 0
    },
  ),
  excludedFiles = emptyList(),
  includedExtensions = emptyList(),
  excludedExtensions = listOf(
    object : UserPresetExtension {
      override val id: Int = 0
      override val extension: String = "tmp"
      override val basePresetId: Int = 0
      override val isIncluded: Boolean = false
    },
    object : UserPresetExtension {
      override val id: Int = 1
      override val extension: String = "cache"
      override val basePresetId: Int = 0
      override val isIncluded: Boolean = false
    },
  ),
  isDuplicateHashesEnabled = true,
  isDuplicateFileNamesEnabled = true,
  isDuplicateFolderNamesEnabled = true,
  isEmptyFoldersEnabled = true,
  isEmptyFilesEnabled = false,
  isSimilarImagesEnabled = true,
  similarImagesMinSimilarity = 0.8f,
  isSimilarImagesImproveAccuracy = true,
  isSimilarVideosEnabled = true,
  similarVideosMinimalHashSimilarity = 0.8f,
  similarVideosMinimalFrameSimilarity = 0.8f,
  similarVideosFPS = 24,
)
