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

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.loading
import indexxo.composeapp.generated.resources.search_created
import indexxo.composeapp.generated.resources.search_created_at
import indexxo.composeapp.generated.resources.search_created_between
import indexxo.composeapp.generated.resources.search_empty_results
import indexxo.composeapp.generated.resources.search_file_type
import indexxo.composeapp.generated.resources.search_include_contents
import indexxo.composeapp.generated.resources.search_modified
import indexxo.composeapp.generated.resources.search_modified_at
import indexxo.composeapp.generated.resources.search_modified_between
import indexxo.composeapp.generated.resources.search_results
import io.github.sadellie.indexxo.core.common.localDateTimeNow
import io.github.sadellie.indexxo.core.common.openInSystem
import io.github.sadellie.indexxo.core.designsystem.component.DropDownChip
import io.github.sadellie.indexxo.core.designsystem.component.FilterChip
import io.github.sadellie.indexxo.core.designsystem.component.OptionsMenuButton
import io.github.sadellie.indexxo.core.designsystem.component.OptionsMenuItem
import io.github.sadellie.indexxo.core.designsystem.component.RowWithScrollbar
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.ArrowDownward
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.HourglassTop
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SearchOff
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Sort
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObjectImpl
import io.github.sadellie.indexxo.core.model.SortType
import io.github.sadellie.indexxo.feature.search.component.DateRangeSelectorSheet
import io.github.sadellie.indexxo.feature.search.component.FileTypeSelectorSheet
import kotlinx.coroutines.launch
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenViewReady(
  uiState: SearchScreenUIState,
  textQuery: TextFieldValue,
  onTextQueryChange: (TextFieldValue) -> Unit,
  onSearchOptionsChange: (SearchOptions) -> Unit,
  navigateUp: () -> Unit,
) {
  val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

  Scaffold(
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      val resultSize = remember(uiState.result) {
        when (uiState.result) {
          SearchResultState.Empty,
          SearchResultState.Loading -> 0

          is SearchResultState.Ready -> uiState.result.indexedObjects.size
        }
      }

      SearchScreenTopBar(
        textQuery = textQuery,
        onTextQueryChange = onTextQueryChange,
        navigateUp = navigateUp,
        scrollBehavior = scrollBehavior,
        searchOptions = uiState.searchOptions,
        onSearchOptionsChange = onSearchOptionsChange,
        resultSize = resultSize,
      )
    },
  ) { paddingValues ->
    Crossfade(modifier = Modifier.padding(paddingValues), targetState = uiState.result) { result ->
      when (result) {
        SearchResultState.Empty -> SearchScreenEmptyResult(Modifier.fillMaxSize())

        SearchResultState.Loading -> SearchScreenLoadingResult(Modifier.fillMaxSize())

        is SearchResultState.Ready -> {
          val listState = rememberLazyListState()

          RowWithScrollbar(listState = listState) {
            LazyColumn(modifier = Modifier.weight(1f), state = listState) {
              items(result.indexedObjects) { indexedObject ->
                SearchFileItem(
                  modifier = Modifier.padding(horizontal = 16.dp),
                  item = indexedObject,
                  optionMenuOnOpen = { indexedObject.path.openInSystem() },
                  optionMenuOnShowInExplorer = { indexedObject.path.parent?.openInSystem() },
                )
              }
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreenTopBar(
  textQuery: TextFieldValue,
  onTextQueryChange: (TextFieldValue) -> Unit,
  navigateUp: () -> Unit,
  scrollBehavior: TopAppBarScrollBehavior,
  searchOptions: SearchOptions,
  onSearchOptionsChange: (SearchOptions) -> Unit,
  resultSize: Int,
) {
  var searchSheetState by remember { mutableStateOf(SearchSheetState.NONE) }
  val sheetState = rememberModalBottomSheetState()
  val coroutineScope = rememberCoroutineScope()
  fun hideSheet() =
    coroutineScope
      .launch { sheetState.hide() }
      .invokeOnCompletion { if (!sheetState.isVisible) searchSheetState = SearchSheetState.NONE }

  Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
    SearchBar(
      query = textQuery,
      onQueryChange = { onTextQueryChange(it) },
      navigateUp = navigateUp,
      scrollBehavior = scrollBehavior,
    )

    FiltersRow(
      searchOptions = searchOptions,
      onFileTypeClick = { searchSheetState = SearchSheetState.FILE_TYPE },
      onCreatedDateRangeClick = { searchSheetState = SearchSheetState.CREATED_DATE_RANGE },
      onModifiedDateRangeClick = { searchSheetState = SearchSheetState.MODIFIED_DATE_RANGE },
      onIncludeContentsClick = {
        onSearchOptionsChange(searchOptions.copy(includeContents = it))
      },
    )

    SearchResultControls(
      resultSize = resultSize,
      onSearchOptionsChange = onSearchOptionsChange,
      searchOptions = searchOptions,
    )
  }

  when (searchSheetState) {
    SearchSheetState.FILE_TYPE ->
      FileTypeSelectorSheet(
        sheetState = sheetState,
        onDismissRequest = ::hideSheet,
        selectedFileCategories = searchOptions.fileCategories,
        onSelectedFileTypesChange = {
          onSearchOptionsChange(searchOptions.copy(fileCategories = it))
          hideSheet()
        },
      )

    SearchSheetState.CREATED_DATE_RANGE ->
      DateRangeSelectorSheet(
        sheetState = sheetState,
        onDismissRequest = ::hideSheet,
        title = stringResource(Res.string.search_created),
        dateRange = searchOptions.createdDateRange,
        onDateRangeChange = {
          onSearchOptionsChange(searchOptions.copy(createdDateRange = it))
          hideSheet()
        },
      )

    SearchSheetState.MODIFIED_DATE_RANGE ->
      DateRangeSelectorSheet(
        sheetState = sheetState,
        onDismissRequest = ::hideSheet,
        title = stringResource(Res.string.search_modified),
        dateRange = searchOptions.modifiedDateRange,
        onDateRangeChange = {
          onSearchOptionsChange(searchOptions.copy(modifiedDateRange = it))
          hideSheet()
        },
      )

    SearchSheetState.NONE -> Unit
  }
}

@Composable
private fun FiltersRow(
  searchOptions: SearchOptions,
  onFileTypeClick: () -> Unit,
  onCreatedDateRangeClick: () -> Unit,
  onModifiedDateRangeClick: () -> Unit,
  onIncludeContentsClick: (Boolean) -> Unit,
) =
  LazyRow(
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    item("fileCategories") {
      val label =
        when (searchOptions.fileCategories.size) {
          0 -> stringResource(Res.string.search_file_type)
          1 -> stringResource(searchOptions.fileCategories.first().res)
          else -> "${stringResource(searchOptions.fileCategories.first().res)}+1"
        }

      DropDownChip(
        onClick = onFileTypeClick,
        label = label,
        active = searchOptions.fileCategories.isNotEmpty(),
      )
    }

    item("created_date_range") {
      val createdAtString = stringResource(Res.string.search_created_at)
      val createdBetweenString = stringResource(Res.string.search_created_between)
      val label =
        remember(searchOptions.createdDateRange) {
          val formattedLabel = searchOptions.createdDateRange?.toFormattedString()
          if (formattedLabel == null) createdAtString else "$createdBetweenString: $formattedLabel"
        }

      DropDownChip(
        onClick = onCreatedDateRangeClick,
        label = label,
        active = searchOptions.createdDateRange != null,
      )
    }

    item("modified_date_range") {
      val modifiedAtString = stringResource(Res.string.search_modified_at)
      val modifiedBetweenString = stringResource(Res.string.search_modified_between)
      val label =
        remember(searchOptions.modifiedDateRange) {
          val formattedLabel = searchOptions.modifiedDateRange?.toFormattedString()
          if (formattedLabel == null) modifiedAtString
          else "$modifiedBetweenString: $formattedLabel"
        }

      DropDownChip(
        onClick = onModifiedDateRangeClick,
        label = label,
        active = searchOptions.modifiedDateRange != null,
      )
    }

    item("search_contents") {
      FilterChip(
        selected = searchOptions.includeContents,
        onClick = { onIncludeContentsClick(!searchOptions.includeContents) },
        label = stringResource(Res.string.search_include_contents),
      )
    }
  }

@Composable
private fun SearchResultControls(
  resultSize: Int,
  onSearchOptionsChange: (SearchOptions) -> Unit,
  searchOptions: SearchOptions
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidth().padding(16.dp, 4.dp),
  ) {
    Text(
      text = "${stringResource(Res.string.search_results)}: $resultSize",
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.weight(1f),
    )
    IconButton(
      onClick = {
        onSearchOptionsChange(
          searchOptions.copy(sortDescending = !searchOptions.sortDescending),
        )
      },
    ) {
      val rotation =
        animateFloatAsState(if (searchOptions.sortDescending) 0f else 180f)
      Icon(
        imageVector = SymbolsRounded.ArrowDownward,
        contentDescription = null,
        modifier = Modifier.rotate(rotation.value),
      )
    }
    OptionsMenuButton(imageVector = SymbolsRounded.Sort) {
      SortType.entries.forEach {
        OptionsMenuItem(
          label = stringResource(it.resName),
          onClick = { onSearchOptionsChange(searchOptions.copy(sortType = it)) },
        )
      }
    }
  }
}

private enum class SearchSheetState {
  NONE,
  FILE_TYPE,
  CREATED_DATE_RANGE,
  MODIFIED_DATE_RANGE,
}

@Composable
private fun SearchScreenEmptyResult(modifier: Modifier) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
  ) {
    Icon(
      imageVector = SymbolsRounded.SearchOff,
      contentDescription = null,
      modifier = Modifier.size(48.dp),
    )
    Text(
      text = stringResource(Res.string.search_empty_results),
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.bodyLarge,
    )
  }
}

@Composable
private fun SearchScreenLoadingResult(modifier: Modifier) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
  ) {
    Icon(
      imageVector = SymbolsRounded.HourglassTop,
      contentDescription = null,
      modifier = Modifier.size(48.dp),
    )
    Text(
      text = stringResource(Res.string.loading),
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.bodyLarge,
    )
  }
}

@Composable
@Preview
private fun PreviewSearchScreenViewReady() {
  SearchScreenViewReady(
    uiState =
      SearchScreenUIState(
        result =
          SearchResultState.Ready(
            List(30) {
              IndexedObjectImpl(
                path = "/path/to/document$it.txt".toPath(),
                parentPath = null,
                sizeBytes = 0L,
                fileCategory = FileCategory.OTHER,
                createdDate = localDateTimeNow(),
                modifiedDate = localDateTimeNow(),
              )
            },
          ),
        searchOptions =
          SearchOptions(
            fileCategories = FileCategory.entries.take(0),
            createdDateRange = localDateTimeNow() to localDateTimeNow(),
            modifiedDateRange = null,
            includeContents = true,
            sortType = SortType.FILE_NAME,
            sortDescending = false,
          ),
      ),
    textQuery = TextFieldValue("Text"),
    onTextQueryChange = {},
    onSearchOptionsChange = {},
    navigateUp = {},
  )
}

@Composable
@Preview
private fun PreviewSearchScreenViewLoadingList() {
  SearchScreenViewReady(
    uiState =
      SearchScreenUIState(
        result = SearchResultState.Loading,
        searchOptions =
          SearchOptions(
            fileCategories = FileCategory.entries.take(0),
            createdDateRange = localDateTimeNow() to localDateTimeNow(),
            modifiedDateRange = null,
            includeContents = true,
            sortType = SortType.FILE_NAME,
            sortDescending = false,
          ),
      ),
    textQuery = TextFieldValue("Text"),
    onTextQueryChange = {},
    onSearchOptionsChange = {},
    navigateUp = {},
  )
}
