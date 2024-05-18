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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDateTime
import okio.Path.Companion.toPath

class DuplicateHashTest {
  private val testFile1 = IndexedObjectImpl(
    path = "test1".toPath(),
    parentPath = null,
    sizeBytes = 25,
    fileCategory = FileCategory.DOCUMENT,
    createdDate = LocalDateTime.parse("2020-01-01T01:00:00"),
    modifiedDate = LocalDateTime.parse("2020-01-01T01:00:00"),
  )
  private val testFile2 = IndexedObjectImpl(
    path = "test2".toPath(),
    parentPath = null,
    sizeBytes = 25,
    fileCategory = FileCategory.DOCUMENT,
    createdDate = LocalDateTime.parse("2020-01-02T01:00:00"),
    modifiedDate = LocalDateTime.parse("2020-01-02T01:00:00"),
  )
  private val testFile3 = IndexedObjectImpl(
    path = "test3".toPath(),
    parentPath = null,
    sizeBytes = 25,
    fileCategory = FileCategory.DOCUMENT,
    createdDate = LocalDateTime.parse("2020-01-02T01:00:00"),
    modifiedDate = LocalDateTime.parse("2020-01-03T01:00:00"),
  )
  private val testFile4 = IndexedObjectImpl(
    path = "test4".toPath(),
    parentPath = null,
    sizeBytes = 25,
    fileCategory = FileCategory.DOCUMENT,
    createdDate = LocalDateTime.parse("2020-01-01T01:00:00"),
    modifiedDate = LocalDateTime.parse("2020-01-01T01:00:00"),
  )
  private val testFile5 = IndexedObjectImpl(
    path = "test5".toPath(),
    parentPath = null,
    sizeBytes = 25,
    fileCategory = FileCategory.DOCUMENT,
    createdDate = LocalDateTime.parse("2020-01-02T01:00:00"),
    modifiedDate = LocalDateTime.parse("2020-01-02T01:00:00"),
  )
  private val testFile6 = IndexedObjectImpl(
    path = "test6".toPath(),
    parentPath = null,
    sizeBytes = 25,
    fileCategory = FileCategory.DOCUMENT,
    createdDate = LocalDateTime.parse("2020-01-02T01:00:00"),
    modifiedDate = LocalDateTime.parse("2020-01-03T01:00:00"),
  )

  @Test
  fun testRemove() {
    val duplicates = listOf(
      DuplicateHash(
        duplicates = listOf(testFile1, testFile2, testFile3),
        totalSizeBytes = 75,
        hash = 100,
      ),
      DuplicateHash(
        duplicates = listOf(testFile4, testFile5, testFile6),
        totalSizeBytes = 75,
        hash = 200,
      ),
    )

    val actual = duplicates.remove(setOf(testFile3.path))

    val expected = listOf(
      DuplicateHash(
        duplicates = listOf(testFile4, testFile5, testFile6),
        totalSizeBytes = 75,
        hash = 200,
      ),
      DuplicateHash(
        duplicates = listOf(testFile1, testFile2),
        totalSizeBytes = 50,
        hash = 100,
      ),
    )

    // remove test3 item and reorder since first group is now smaller
    assertEquals(expected, actual)
  }

  @Test
  fun testRemoveOneAndReorder() {
    val duplicates = listOf(
      DuplicateHash(
        duplicates = listOf(testFile1, testFile2, testFile3),
        totalSizeBytes = 75,
        hash = 100,
      ),
      DuplicateHash(
        duplicates = listOf(testFile4, testFile5, testFile6),
        totalSizeBytes = 75,
        hash = 200,
      ),
    )

    val actual = duplicates.retain(
      listOf(testFile1, testFile2, testFile4, testFile5, testFile6).map { it.path }.toSet(),
    )

    val expected = listOf(
      DuplicateHash(
        duplicates = listOf(testFile4, testFile5, testFile6),
        totalSizeBytes = 75,
        hash = 200,
      ),
      DuplicateHash(
        duplicates = listOf(testFile1, testFile2),
        totalSizeBytes = 50,
        hash = 100,
      ),
    )

    // remove one item and reorder since first group is now smaller
    assertEquals(expected, actual)
  }
}
