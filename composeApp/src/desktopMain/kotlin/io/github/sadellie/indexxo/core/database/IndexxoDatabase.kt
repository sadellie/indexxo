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

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.sadellie.indexxo.core.database.model.BaseUserPresetEntity
import io.github.sadellie.indexxo.core.database.model.UserPresetExtensionEntity
import io.github.sadellie.indexxo.core.database.model.UserPresetPathEntity
import kotlinx.coroutines.Dispatchers
import okio.Path

@Database(
  version = 1,
  exportSchema = true,
  entities = [BaseUserPresetEntity::class, UserPresetPathEntity::class, UserPresetExtensionEntity::class],
)
@TypeConverters(DatabaseConverter::class)
abstract class IndexxoDatabase : RoomDatabase() {
  abstract fun dao(): IndexxoDatabaseDao
}

fun getIndexxoDatabase(dbPath: Path): IndexxoDatabase = Room
  .databaseBuilder<IndexxoDatabase>(dbPath.toFile().absolutePath)
  .fallbackToDestructiveMigration(true)
  .fallbackToDestructiveMigrationOnDowngrade(true)
  .setDriver(BundledSQLiteDriver())
  .setQueryCoroutineContext(Dispatchers.IO)
  .build()

const val MAIN_DB_NAME = "indexxo.db"
