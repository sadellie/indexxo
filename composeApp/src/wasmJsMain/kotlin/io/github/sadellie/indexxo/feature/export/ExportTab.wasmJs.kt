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

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.name
import indexxo.composeapp.generated.resources.path
import indexxo.composeapp.generated.resources.size
import io.github.sadellie.indexxo.core.designsystem.component.TableColumnInfo
import io.github.sadellie.indexxo.core.designsystem.component.TableColumnSort
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.fakeIndex
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okio.Path

@Composable
actual fun Screen.getExportTabModel() = rememberScreenModel { ExportTabModel() }

actual class ExportTabModel : ScreenModel {
  actual val uiState: StateFlow<ExportTabUIState?> = MutableStateFlow(
    ExportTabUIState(
      tableColumnInfos = listOf(
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
      ),
      tableData = fakeIndex.filter { it.fileCategory != FileCategory.FOLDER },
      exportProgress = ExportProgress.None,
    ),
  )

  actual fun onTableColumnInfosChange(tableColumnInfos: List<TableColumnInfo>) = Unit

  actual fun onExportCancel() = Unit

  actual fun onExportStart(path: Path) = Unit
}
