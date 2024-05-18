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

import io.github.sadellie.indexxo.core.common.maxSystemThreads
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObjectImpl
import io.github.sadellie.indexxo.core.model.SortType
import io.github.sadellie.indexxo.core.model.search
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals

class IndexedObjectTest {

  @Test
  fun testSearch() = runBlocking {
    val document1 = IndexedObjectImpl(
      path = "document1.txt".toPath(),
      parentPath = null,
      sizeBytes = 256L,
      fileCategory = FileCategory.DOCUMENT,
      createdDate = LocalDateTime.parse("2020-01-01T01:00:00"),
      modifiedDate = LocalDateTime.parse("2020-01-01T01:00:00"),
    )

    val document2 = IndexedObjectImpl(
      path = "document2.txt".toPath(),
      parentPath = null,
      sizeBytes = 256L,
      fileCategory = FileCategory.DOCUMENT,
      createdDate = LocalDateTime.parse("2020-01-02T01:00:00"),
      modifiedDate = LocalDateTime.parse("2020-01-02T01:00:00"),
    )

    val image1 = IndexedObjectImpl(
      path = "image1.png".toPath(),
      parentPath = null,
      sizeBytes = 256L,
      fileCategory = FileCategory.IMAGE,
      createdDate = LocalDateTime.parse("2020-01-03T01:00:00"),
      modifiedDate = LocalDateTime.parse("2020-01-03T01:00:00"),
    )

    val index = listOf(document1, document2, image1)

    // Expect all if nothing is specified in filters
    var expected = listOf(document1, document2, image1)
    var actual = index.search(
      textQuery = "",
      fileCategories = emptyList(),
      createdDateRange = null,
      modifiedDateRange = null,
      includeContents = false,
      sortType = SortType.CREATED_AT,
      sortDescending = false,
      maxThreads = maxSystemThreads,
    )
    assertEquals(expected, actual)

    // Unreal text filter
    expected = emptyList()
    actual = index.search(
      textQuery = "test",
      fileCategories = emptyList(),
      createdDateRange = null,
      modifiedDateRange = null,
      includeContents = false,
      sortType = SortType.CREATED_AT,
      sortDescending = false,
      maxThreads = maxSystemThreads,
    )
    assertEquals(expected, actual)

    // Valid text filter
    expected = listOf(image1)
    actual = index.search(
      textQuery = "image",
      fileCategories = emptyList(),
      createdDateRange = null,
      modifiedDateRange = null,
      includeContents = false,
      sortType = SortType.CREATED_AT,
      sortDescending = false,
      maxThreads = maxSystemThreads,
    )
    assertEquals(expected, actual)
  }
}
