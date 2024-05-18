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

package io.github.sadellie.indexxo.feature.export

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import co.touchlab.kermit.Logger
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.name
import indexxo.composeapp.generated.resources.path
import indexxo.composeapp.generated.resources.size
import io.github.sadellie.indexxo.core.common.localDateTimeNow
import io.github.sadellie.indexxo.core.common.stateIn
import io.github.sadellie.indexxo.core.data.IndexedObjectRepository
import io.github.sadellie.indexxo.core.designsystem.component.TableColumnInfo
import io.github.sadellie.indexxo.core.designsystem.component.TableColumnSort
import io.github.sadellie.indexxo.core.designsystem.component.getCurrentSorting
import io.github.sadellie.indexxo.core.designsystem.theme.Previewer
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObjectImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.Path
import okio.Path.Companion.toPath

@Composable
actual fun Screen.getExportTabModel() = koinScreenModel<ExportTabModel>()

actual class ExportTabModel(val indexedObjectRepository: IndexedObjectRepository) : ScreenModel {
  private var _exportJob: Job? = null
  private val _tableColumnInfos = MutableStateFlow(exportTabDefaultTableColumnInfos)
  @OptIn(ExperimentalCoroutinesApi::class)
  private val _currentSorting: Flow<Pair<Int, Boolean>> =
    _tableColumnInfos.mapLatest(List<TableColumnInfo>::getCurrentSorting)
  private val _tableData =
    combine(indexedObjectRepository.indexedObjects, _currentSorting) {
        indexedObjects,
        (columnIndex, isDescending) ->
      var files = indexedObjects.filter { it.fileCategory != FileCategory.FOLDER }

        files =
          when (columnIndex) {
            1 -> files.sortedBy { it.sizeBytes }
            2 -> files.sortedBy { it.path.toString().lowercase() }
            else -> files.sortedBy { it.path.name.lowercase() }
          }

        if (isDescending) {
          files = files.reversed()
        }

        files
      }
      .flowOn(Dispatchers.Default)
  private val _exportProgress = MutableStateFlow<ExportProgress>(ExportProgress.None)

  actual fun onTableColumnInfosChange(tableColumnInfos: List<TableColumnInfo>) =
    _tableColumnInfos.update { tableColumnInfos }

  actual fun onExportCancel() {
    _exportJob?.cancel()
    _exportProgress.update { ExportProgress.None }
  }

  actual fun onExportStart(path: Path) {
    _exportJob?.cancel()
    _exportJob =
      screenModelScope.launch {
        _exportProgress.update { ExportProgress.InProgress }
        try {
          indexedObjectRepository.export(path)
          _exportProgress.update { ExportProgress.Success(path) }
        } catch (e: Exception) {
          Logger.e(e) { "Failed to export" }
          _exportProgress.update { ExportProgress.Error(e.message ?: "error") }
        }
      }
  }

  actual val uiState =
    combine(_tableColumnInfos, _tableData, _exportProgress) {
        tableColumnInfos,
        tableData,
        exportProgress, ->
        ExportTabUIState(
          tableColumnInfos = tableColumnInfos,
          tableData = tableData,
          exportProgress = exportProgress,
        )
      }
      .stateIn(screenModelScope, null)
}

val exportTabDefaultTableColumnInfos by lazy {
  listOf(
    TableColumnInfo(
      name = Res.string.name,
      width = 240.dp,
      draggable = true,
      sortState = TableColumnSort.ACTIVE_ASCENDING,
    ),
    TableColumnInfo(
      name = Res.string.size,
      width = 120.dp,
      draggable = true,
      sortState = TableColumnSort.INACTIVE,
    ),
    TableColumnInfo(
      name = Res.string.path,
      width = 360.dp,
      draggable = true,
      sortState = TableColumnSort.INACTIVE,
    ),
  )
}

@Composable
@Preview
private fun PreviewExportTabView() = Previewer {
  ExportTabView(
    title = "Export",
    uiState =
    ExportTabUIState(
      tableColumnInfos = exportTabDefaultTableColumnInfos,
      tableData =
      List(30) {
        IndexedObjectImpl(
          path = "/path/to/document$it.txt".toPath(),
          parentPath = null,
          sizeBytes = 0L,
          fileCategory = FileCategory.DOCUMENT,
          createdDate = localDateTimeNow(),
          modifiedDate = localDateTimeNow(),
        )
      },
      exportProgress = ExportProgress.None,
    ),
    onTableColumnInfosChange = {},
    onExportStart = {},
    onExportCancel = {},
  )
}

@Composable
@Preview
private fun PreviewExportTabViewEmpty() = Previewer {
  ExportTabView(
    title = "Export",
    uiState =
    ExportTabUIState(
      tableColumnInfos = exportTabDefaultTableColumnInfos,
      tableData = emptyList(),
      exportProgress = ExportProgress.None,
    ),
    onTableColumnInfosChange = {},
    onExportStart = {},
    onExportCancel = {},
  )
}
