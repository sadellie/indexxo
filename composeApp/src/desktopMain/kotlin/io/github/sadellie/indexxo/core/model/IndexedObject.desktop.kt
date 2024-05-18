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

import co.touchlab.kermit.Logger
import io.github.sadellie.indexxo.core.common.MAX_BUFFER_SIZE
import io.github.sadellie.indexxo.core.common.isBetween
import io.github.sadellie.indexxo.core.common.mapAsyncAwaitAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.apache.tika.parser.ParsingReader
import java.io.File

suspend fun List<IndexedObject>.search(
  textQuery: String,
  fileCategories: List<FileCategory>,
  createdDateRange: LocalDateTimeRange?,
  modifiedDateRange: LocalDateTimeRange?,
  includeContents: Boolean,
  sortType: SortType,
  sortDescending: Boolean,
  maxThreads: Int,
): List<IndexedObject> = withContext(Dispatchers.Default) {
  val indexedObjects = this@search
  val textQueryLowered = textQuery.lowercase()
  val isTextQueryMatchEnabled = textQueryLowered.isNotBlank()

  val isFileCategoriesMatchEnabled =
    fileCategories.isNotEmpty() && !fileCategories.containsAll(FileCategory.entries)

  val isCreatedDateRangeMatchEnabled = createdDateRange != null
  val isModifiedDateRangeMatchEnabled = modifiedDateRange != null

  var filtererResult: List<IndexedObject> = if (
    !isTextQueryMatchEnabled &&
    !isFileCategoriesMatchEnabled &&
    !isCreatedDateRangeMatchEnabled &&
    !isModifiedDateRangeMatchEnabled
  ) {
    // all checks are disabled
    Logger.v(TAG) { "All checks are disabled, skip filters" }
    indexedObjects
  } else {
    val semaphore = Semaphore(maxThreads)
    val timeZone = TimeZone.currentSystemDefault()
    indexedObjects.mapAsyncAwaitAll(this@withContext, semaphore) { indexedObject ->
      Logger.v(TAG) { "Search in ${indexedObject.path}" }

      if (isTextQueryMatchEnabled) {
        // apply text filter
        val textFilterPassed = filterByText(textQueryLowered, indexedObject, includeContents)
        if (!textFilterPassed) return@mapAsyncAwaitAll null
      }

      if (isFileCategoriesMatchEnabled) {
        // apply file categories filter
        if (indexedObject.fileCategory !in fileCategories) return@mapAsyncAwaitAll null
      }

      if (createdDateRange != null) {
        // apply created date filter
        val createdDateRangeCheckPassed =
          filterByDate(timeZone, indexedObject.createdDate, createdDateRange)
        if (!createdDateRangeCheckPassed) return@mapAsyncAwaitAll null
      }

      if (modifiedDateRange != null) {
        // apply modified date filter
        val modifiedDateRangeCheckPassed =
          filterByDate(timeZone, indexedObject.modifiedDate, modifiedDateRange)
        if (!modifiedDateRangeCheckPassed) return@mapAsyncAwaitAll null
      }

      // All checks passed
      return@mapAsyncAwaitAll indexedObject
    }
      .filterNotNull()
  }

  filtererResult =
    when (sortType) {
      SortType.FILE_NAME -> filtererResult.sortedBy { it.path.name }
      SortType.FILE_SIZE -> filtererResult.sortedBy { it.sizeBytes }
      SortType.CREATED_AT -> filtererResult.sortedBy { it.createdDate }
      SortType.MODIFIED_AT -> filtererResult.sortedBy { it.modifiedDate }
    }

  if (sortDescending) {
    filtererResult = filtererResult.reversed()
  }

  return@withContext filtererResult
}

private suspend fun filterByText(
  textQueryLowered: String,
  indexedObject: IndexedObject,
  includeContents: Boolean,
): Boolean {
  val hasMatchInName = textQueryLowered in indexedObject.path.name.lowercase()
  if (!hasMatchInName) {
    if (!includeContents) return false
    if (indexedObject.fileCategory == FileCategory.FOLDER) return false

    val hasMatchInContent = withContext(Dispatchers.IO) { textQueryLowered in indexedObject }
    if (!hasMatchInContent) return false
  }
  return true
}

private fun filterByDate(
  timeZone: TimeZone,
  dateToCheck: LocalDateTime,
  dateRange: LocalDateTimeRange,
): Boolean {
  val rangeStart = dateRange.first
  val rangeEnd =
    dateRange.second
      .toInstant(timeZone)
      .plus(1, DateTimeUnit.DAY, timeZone)
      .toLocalDateTime(timeZone)
  return dateToCheck.isBetween(rangeStart, rangeEnd)
}

private operator fun IndexedObject.contains(other: CharSequence): Boolean {
  if (this.fileCategory == FileCategory.FOLDER) return false
  return try {
    // Try to find in content
    findInContent(this.path.toFile(), other)
  } catch (e: Exception) {
    Logger.e(TAG, e) { "Failed to check file content" }
    false
  }
}

private fun findInContent(
  file: File,
  query: CharSequence,
): Boolean {
  // No check for extension since files without extensions can still be parsed (tika's feature)
  ParsingReader(file).buffered(MAX_BUFFER_SIZE).use { stream ->
    var currentLine = stream.readLine()
    while (currentLine != null) {
      if (query in currentLine.lowercase()) {
        return true
      }
      currentLine = stream.readLine()
    }
  }
  return false
}

private const val TAG = "IndexedObject.Search"
