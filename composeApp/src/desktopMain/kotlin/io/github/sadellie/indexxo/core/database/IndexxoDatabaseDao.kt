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

package io.github.sadellie.indexxo.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.github.sadellie.indexxo.core.database.model.BaseUserPresetEntity
import io.github.sadellie.indexxo.core.database.model.UserPresetPathEntity
import io.github.sadellie.indexxo.core.database.model.UserPresetEntity
import io.github.sadellie.indexxo.core.database.model.UserPresetExtensionEntity
import kotlinx.coroutines.flow.Flow
import okio.Path

@Dao
interface IndexxoDatabaseDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBaseUserPreset(baseUserPresetEntity: BaseUserPresetEntity): Long

  @Update
  suspend fun updateBaseUserPreset(baseUserPresetEntity: BaseUserPresetEntity)

  @Transaction
  @Query("SELECT * FROM base_user_preset")
  fun getUserPresets(): Flow<List<UserPresetEntity>>

  @Transaction
  @Query("SELECT * FROM base_user_preset WHERE id = :id")
  fun getUserPreset(id: Int): Flow<UserPresetEntity?>

  @Delete
  suspend fun deleteBaseUserPreset(baseUserPresetEntity: BaseUserPresetEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUserPresetPath(userPresetPath: UserPresetPathEntity)

  @Query("UPDATE user_preset_path SET path = :path WHERE id = :id")
  suspend fun updateUserPresetPath(id: Int, path: Path)

  @Query("DELETE FROM user_preset_path WHERE id = :id")
  suspend fun deleteUserPresetPath(id: Int)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUserPresetExtension(extensionEntity: UserPresetExtensionEntity)

  @Query("UPDATE user_preset_extension SET extension = :extension WHERE id = :id")
  suspend fun updateUserPresetExtension(id: Int, extension: String)

  @Query("DELETE FROM user_preset_extension WHERE id = :id")
  suspend fun deleteUserPresetExtension(id: Int)
}
