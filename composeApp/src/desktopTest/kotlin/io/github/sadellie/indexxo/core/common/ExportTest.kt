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

package io.github.sadellie.indexxo.core.common

import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObject
import io.github.sadellie.indexxo.core.model.IndexedObjectImpl
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals

class ExportTest {
  private val fakeIndex: List<IndexedObject> =
    listOf(
      IndexedObjectImpl(
        path = "/folder".toPath(),
        parentPath = null,
        sizeBytes = 0,
        fileCategory = FileCategory.FOLDER,
        createdDate = LocalDateTime.parse("2020-01-01T01:02:03"),
        modifiedDate = LocalDateTime.parse("2020-01-01T01:02:03"),
      ),
      IndexedObjectImpl(
        path = "image.png".toPath(),
        parentPath = null,
        sizeBytes = 0,
        fileCategory = FileCategory.IMAGE,
        createdDate = LocalDateTime.parse("2020-01-01T01:02:03"),
        modifiedDate = LocalDateTime.parse("2020-01-01T01:02:03"),
      ),
      IndexedObjectImpl(
        path = "video.mov".toPath(),
        parentPath = null,
        sizeBytes = 0,
        fileCategory = FileCategory.VIDEO,
        createdDate = LocalDateTime.parse("2020-01-01T01:02:03"),
        modifiedDate = LocalDateTime.parse("2020-01-01T01:02:03"),
      ),
      IndexedObjectImpl(
        path = "document.txt".toPath(),
        parentPath = null,
        sizeBytes = 0,
        fileCategory = FileCategory.DOCUMENT,
        createdDate = LocalDateTime.parse("2020-01-01T01:02:03"),
        modifiedDate = LocalDateTime.parse("2020-01-01T01:02:03"),
      ),
      IndexedObjectImpl(
        path = "audio.wav".toPath(),
        parentPath = null,
        sizeBytes = 0,
        fileCategory = FileCategory.AUDIO,
        createdDate = LocalDateTime.parse("2020-01-01T01:02:03"),
        modifiedDate = LocalDateTime.parse("2020-01-01T01:02:03"),
      ),
      IndexedObjectImpl(
        path = "other.qwe".toPath(),
        parentPath = null,
        sizeBytes = 0,
        fileCategory = FileCategory.OTHER,
        createdDate = LocalDateTime.parse("2020-01-01T01:02:03"),
        modifiedDate = LocalDateTime.parse("2020-01-01T01:02:03"),
      ),
    )

  @Test
  fun exportLocalIndexedObjects() {
    val expectedJsonElement =
      """
        [{"type":"io.github.sadellie.indexxo.core.model.IndexedObjectImpl","path":"/folder","parentPath":null,"sizeBytes":0,"fileCategory":"FOLDER","createdDate":"2020-01-01T01:02:03","modifiedDate":"2020-01-01T01:02:03"},{"type":"io.github.sadellie.indexxo.core.model.IndexedObjectImpl","path":"image.png","parentPath":null,"sizeBytes":0,"fileCategory":"IMAGE","createdDate":"2020-01-01T01:02:03","modifiedDate":"2020-01-01T01:02:03"},{"type":"io.github.sadellie.indexxo.core.model.IndexedObjectImpl","path":"video.mov","parentPath":null,"sizeBytes":0,"fileCategory":"VIDEO","createdDate":"2020-01-01T01:02:03","modifiedDate":"2020-01-01T01:02:03"},{"type":"io.github.sadellie.indexxo.core.model.IndexedObjectImpl","path":"document.txt","parentPath":null,"sizeBytes":0,"fileCategory":"DOCUMENT","createdDate":"2020-01-01T01:02:03","modifiedDate":"2020-01-01T01:02:03"},{"type":"io.github.sadellie.indexxo.core.model.IndexedObjectImpl","path":"audio.wav","parentPath":null,"sizeBytes":0,"fileCategory":"AUDIO","createdDate":"2020-01-01T01:02:03","modifiedDate":"2020-01-01T01:02:03"},{"type":"io.github.sadellie.indexxo.core.model.IndexedObjectImpl","path":"other.qwe","parentPath":null,"sizeBytes":0,"fileCategory":"OTHER","createdDate":"2020-01-01T01:02:03","modifiedDate":"2020-01-01T01:02:03"}]
    """
        .trimIndent()
    val jsonElement = indexedObjectJson.encodeToString(fakeIndex)

    assertEquals(expectedJsonElement, jsonElement)
  }
}
