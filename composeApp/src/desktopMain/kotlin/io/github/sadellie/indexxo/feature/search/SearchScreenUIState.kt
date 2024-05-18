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

package io.github.sadellie.indexxo.feature.search

import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObject
import io.github.sadellie.indexxo.core.model.LocalDateTimeRange
import io.github.sadellie.indexxo.core.model.SortType

data class SearchScreenUIState(val result: SearchResultState, val searchOptions: SearchOptions)

data class SearchOptions(
  val fileCategories: List<FileCategory>,
  val createdDateRange: LocalDateTimeRange?,
  val modifiedDateRange: LocalDateTimeRange?,
  val includeContents: Boolean,
  val sortType: SortType,
  val sortDescending: Boolean,
)

sealed class SearchResultState {
  data object Empty : SearchResultState()

  data object Loading : SearchResultState()

  data class Ready(val indexedObjects: List<IndexedObject>) : SearchResultState()
}
