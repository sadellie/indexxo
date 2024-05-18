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

import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObjectImpl
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import okio.Path.Companion.toPath
import org.junit.Test
import kotlin.test.assertEquals

class AnalyzeEmptyFilesTest {
  @Test
  fun findEmptyFiles() = runBlocking {
    val index = listOf(
      IndexedObjectImpl(
        path = "folder1".toPath(),
        parentPath = null,
        sizeBytes = 0,
        fileCategory = FileCategory.FOLDER,
        createdDate = LocalDateTime.parse("2020-01-01T01:00:00"),
        modifiedDate = LocalDateTime.parse("2020-01-01T01:00:00"),
      ),
      IndexedObjectImpl(
        path = "folder1/file1".toPath(),
        parentPath = "folder1".toPath(),
        sizeBytes = 0,
        fileCategory = FileCategory.DOCUMENT,
        createdDate = LocalDateTime.parse("2020-01-01T01:00:00"),
        modifiedDate = LocalDateTime.parse("2020-01-01T01:00:00"),
      ),
      IndexedObjectImpl(
        path = "folder1/file2".toPath(),
        parentPath = "folder1".toPath(),
        sizeBytes = 10,
        fileCategory = FileCategory.DOCUMENT,
        createdDate = LocalDateTime.parse("2020-01-01T01:00:00"),
        modifiedDate = LocalDateTime.parse("2020-01-01T01:00:00"),
      ),
      IndexedObjectImpl(
        path = "folder2".toPath(),
        parentPath = null,
        sizeBytes = 0,
        fileCategory = FileCategory.FOLDER,
        createdDate = LocalDateTime.parse("2020-01-01T01:00:00"),
        modifiedDate = LocalDateTime.parse("2020-01-01T01:00:00"),
      ),
      IndexedObjectImpl(
        path = "folder1/folder1.1".toPath(),
        parentPath = "folder1".toPath(),
        sizeBytes = 0,
        fileCategory = FileCategory.FOLDER,
        createdDate = LocalDateTime.parse("2020-01-01T01:00:00"),
        modifiedDate = LocalDateTime.parse("2020-01-01T01:00:00"),
      ),
    )

    val actual = analyzeEmptyFiles(index) {}
    val expected = listOf(
      IndexedObjectImpl(
        path = "folder1/file1".toPath(),
        parentPath = "folder1".toPath(),
        sizeBytes = 0,
        fileCategory = FileCategory.DOCUMENT,
        createdDate = LocalDateTime.parse("2020-01-01T01:00:00"),
        modifiedDate = LocalDateTime.parse("2020-01-01T01:00:00"),
      ),
    )

    assertEquals(expected, actual)
  }
}
