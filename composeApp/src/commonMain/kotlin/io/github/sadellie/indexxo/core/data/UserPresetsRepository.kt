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
import kotlinx.coroutines.flow.Flow
import okio.Path

interface UserPresetsRepository {
  val allUserPresets: Flow<List<UserPreset>>
  val currentUserPreset: Flow<UserPreset?>
  suspend fun createUserPreset(name: String): Int
  suspend fun updateUserPreset(userPreset: UserPreset)
  suspend fun deleteUserPreset(userPreset: UserPreset)
  suspend fun insertUserPresetPath(path: Path, isIncluded: Boolean, isDirectory: Boolean, basePresetId: Int)
  suspend fun updateUserPresetPath(id: Int, path: Path)
  suspend fun deleteUserPresetPath(id: Int)
  suspend fun addExtension(extension: String, included: Boolean, basePresetId: Int)
  suspend fun editExtension(id: Int, extension: String)
  suspend fun removeExtension(id: Int)
}
