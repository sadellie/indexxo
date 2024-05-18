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

import io.github.sadellie.indexxo.core.model.DuplicateName
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObjectImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import okio.Path.Companion.toPath

class AnalyzeDuplicateNames {
  private val index =
    listOf(
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
        sizeBytes = 32,
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
        path = "folder2/file1".toPath(),
        parentPath = "folder2".toPath(),
        sizeBytes = 32,
        fileCategory = FileCategory.DOCUMENT,
        createdDate = LocalDateTime.parse("2020-01-01T01:00:00"),
        modifiedDate = LocalDateTime.parse("2020-01-01T01:00:00"),
      ),
      IndexedObjectImpl(
        path = "folder2/folder2".toPath(),
        parentPath = "folder2".toPath(),
        sizeBytes = 0,
        fileCategory = FileCategory.FOLDER,
        createdDate = LocalDateTime.parse("2020-01-01T01:00:00"),
        modifiedDate = LocalDateTime.parse("2020-01-01T01:00:00"),
      ),
    )

  @Test
  fun findDuplicateFileNames() = runBlocking {
    val actual = analyzeDuplicateFileNames(index) {}
    val expected =
      listOf(
        DuplicateName(
          duplicates =
            listOf(
              IndexedObjectImpl(
                path = "folder1/file1".toPath(),
                parentPath = "folder1".toPath(),
                sizeBytes = 32,
                fileCategory = FileCategory.DOCUMENT,
                createdDate = LocalDateTime.parse("2020-01-01T01:00:00"),
                modifiedDate = LocalDateTime.parse("2020-01-01T01:00:00"),
              ),
              IndexedObjectImpl(
                path = "folder2/file1".toPath(),
                parentPath = "folder2".toPath(),
                sizeBytes = 32,
                fileCategory = FileCategory.DOCUMENT,
                createdDate = LocalDateTime.parse("2020-01-01T01:00:00"),
                modifiedDate = LocalDateTime.parse("2020-01-01T01:00:00"),
              ),
            ),
          totalSizeBytes = 64,
          name = "file1",
        )
      )

    assertEquals(expected, actual)
  }

  @Test
  fun findDuplicateFolderNames() = runBlocking {
    val actual = analyzeDuplicateFolderNames(index) {}
    val expected =
      listOf(
        DuplicateName(
          duplicates =
            listOf(
              IndexedObjectImpl(
                path = "folder2".toPath(),
                parentPath = null,
                sizeBytes = 0,
                fileCategory = FileCategory.FOLDER,
                createdDate = LocalDateTime.parse("2020-01-01T01:00:00"),
                modifiedDate = LocalDateTime.parse("2020-01-01T01:00:00"),
              ),
              IndexedObjectImpl(
                path = "folder2/folder2".toPath(),
                parentPath = "folder2".toPath(),
                sizeBytes = 0,
                fileCategory = FileCategory.FOLDER,
                createdDate = LocalDateTime.parse("2020-01-01T01:00:00"),
                modifiedDate = LocalDateTime.parse("2020-01-01T01:00:00"),
              ),
            ),
          totalSizeBytes = 0,
          name = "folder2",
        )
      )

    assertEquals(expected, actual)
  }
}
