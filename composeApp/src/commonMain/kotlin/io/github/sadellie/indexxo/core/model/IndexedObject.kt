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

package io.github.sadellie.indexxo.core.model

import io.github.sadellie.indexxo.core.common.LocalDateTimeAsStringSerializer
import io.github.sadellie.indexxo.core.common.PathAsStringSerializer
import io.github.sadellie.indexxo.core.common.moveToTrash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import okio.Path

interface IndexedObject {
  val path: Path
  val parentPath: Path?
  val sizeBytes: Long
  val fileCategory: FileCategory
  val createdDate: LocalDateTime
  val modifiedDate: LocalDateTime
}

@Serializable
data class IndexedObjectImpl(
  @Serializable(PathAsStringSerializer::class) override val path: Path,
  @Serializable(PathAsStringSerializer::class) override val parentPath: Path?,
  override val sizeBytes: Long,
  override val fileCategory: FileCategory,
  @Serializable(LocalDateTimeAsStringSerializer::class) override val createdDate: LocalDateTime,
  @Serializable(LocalDateTimeAsStringSerializer::class) override val modifiedDate: LocalDateTime,
) : IndexedObject

/**
 * Moves to trash given [IndexedObject]s by checking their [IndexedObject.path] and removes them
 * from [this]. Will also check children and remove them too.
 */
suspend fun Iterable<IndexedObject>.moveToTrash(toRemove: Path): List<IndexedObject> =
  withContext(Dispatchers.Default) {
    val indexedObjects = mutableListOf<IndexedObject>()

    for (indexedObject in this@moveToTrash) {
      val toBeRemoved = indexedObject.path.toString().startsWith(toRemove.toString())

      if (toBeRemoved) {
        indexedObject.path.moveToTrash()
      } else {
        indexedObjects.add(indexedObject)
      }
    }

    return@withContext indexedObjects
  }
