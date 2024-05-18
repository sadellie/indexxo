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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.sadellie.indexxo.core.common.stateIn
import io.github.sadellie.indexxo.core.data.IndexedObjectRepository
import io.github.sadellie.indexxo.core.data.UserPresetsRepository
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.SortType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchScreenModel(
  userPresetsRepository: UserPresetsRepository,
  private val indexedObjectRepository: IndexedObjectRepository,
) : ScreenModel {
  init {
    onSearch()
  }

  var textQuery by mutableStateOf(TextFieldValue())
    private set

  private val _result = MutableStateFlow<SearchResultState>(SearchResultState.Loading)

  private val _searchOptions =
    MutableStateFlow(
      SearchOptions(
        fileCategories = FileCategory.entries,
        createdDateRange = null,
        modifiedDateRange = null,
        includeContents = false,
        sortType = SortType.FILE_NAME,
        sortDescending = false,
      )
    )
  private val _preset = userPresetsRepository.currentUserPreset
  private var _searchJob: Job? = null

  val uiState =
    combine(_searchOptions, _result) { searchOptions, result ->
        SearchScreenUIState(result = result, searchOptions = searchOptions)
      }
      .stateIn(screenModelScope, null)

  fun onSearchOptionsChange(searchOptions: SearchOptions) {
    _searchOptions.update { searchOptions }
    onSearch()
  }

  fun onTextQueryChange(textFieldValue: TextFieldValue) {
    if (textQuery.text.lowercase() != textFieldValue.text.lowercase()) {
      textQuery = textFieldValue
      onSearch()
    } else {
      textQuery = textFieldValue
    }
  }

  private fun onSearch() {
    _searchJob?.cancel()
    _searchJob =
      screenModelScope.launch {
        delay(SEARCH_DEBOUNCE_MILLIS)
        _result.update { SearchResultState.Loading }

        val result =
          indexedObjectRepository.search(
            textQuery = textQuery.text,
            fileCategories = _searchOptions.value.fileCategories,
            createdDateRange = _searchOptions.value.createdDateRange,
            modifiedDateRange = _searchOptions.value.modifiedDateRange,
            includeContents = _searchOptions.value.includeContents,
            sortType = _searchOptions.value.sortType,
            sortDescending = _searchOptions.value.sortDescending,
            maxThreads = _preset.first()?.maxThreads ?: return@launch,
          )

        _result.update {
          if (result.isEmpty()) SearchResultState.Empty else SearchResultState.Ready(result)
        }
      }
  }
}

private const val SEARCH_DEBOUNCE_MILLIS = 300L
