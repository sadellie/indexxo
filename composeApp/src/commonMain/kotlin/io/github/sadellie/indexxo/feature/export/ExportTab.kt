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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.dismiss
import indexxo.composeapp.generated.resources.error
import indexxo.composeapp.generated.resources.export_tab_exported
import indexxo.composeapp.generated.resources.export_tab_exported_support
import indexxo.composeapp.generated.resources.export_tab_in_progress
import indexxo.composeapp.generated.resources.export_tab_title
import io.github.sadellie.indexxo.core.common.formatBytes
import io.github.sadellie.indexxo.core.designsystem.LocalFileKitPlatformSettings
import io.github.sadellie.indexxo.core.designsystem.component.EmptyScreen
import io.github.sadellie.indexxo.core.designsystem.component.FileThumbnail
import io.github.sadellie.indexxo.core.designsystem.component.ScaffoldWithTopBar
import io.github.sadellie.indexxo.core.designsystem.component.SimpleAlertDialogDismissOnly
import io.github.sadellie.indexxo.core.designsystem.component.Table
import io.github.sadellie.indexxo.core.designsystem.component.TableCell
import io.github.sadellie.indexxo.core.designsystem.component.TableColumnInfo
import io.github.sadellie.indexxo.core.designsystem.component.TableRow
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Download
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.TableChart
import io.github.sadellie.indexxo.core.designsystem.tableBorders
import io.github.sadellie.indexxo.core.model.IndexedObject
import io.github.vinceglb.filekit.core.FileKit
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.Path
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.stringResource

data object ExportTab : Tab {
  @Composable
  override fun Content() {
    val screenModel = getExportTabModel()
    when (val uiState = screenModel.uiState.collectAsState().value) {
      null -> EmptyScreen()
      else ->
        ExportTabView(
          title = options.title,
          uiState = uiState,
          onTableColumnInfosChange = screenModel::onTableColumnInfosChange,
          onExportStart = screenModel::onExportStart,
          onExportCancel = screenModel::onExportCancel,
        )
    }
  }

  override val options: TabOptions
    @Composable
    get() {
      val title = stringResource(Res.string.export_tab_title)
      val icon = rememberVectorPainter(SymbolsRounded.TableChart)

      return remember(title) { TabOptions(index = 4u, title = title, icon = icon) }
    }
}

@Composable expect fun Screen.getExportTabModel(): ExportTabModel

expect class ExportTabModel : ScreenModel {
  val uiState: StateFlow<ExportTabUIState?>

  fun onTableColumnInfosChange(tableColumnInfos: List<TableColumnInfo>)

  fun onExportCancel()

  fun onExportStart(path: Path)
}

data class ExportTabUIState(
  val tableColumnInfos: List<TableColumnInfo>,
  val tableData: List<IndexedObject>,
  val exportProgress: ExportProgress,
)

sealed class ExportProgress {
  data object None : ExportProgress()

  data object InProgress : ExportProgress()

  data class Error(val message: String) : ExportProgress()

  data class Success(val path: Path) : ExportProgress()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportTabView(
  title: String,
  uiState: ExportTabUIState,
  onTableColumnInfosChange: (List<TableColumnInfo>) -> Unit,
  onExportStart: (Path) -> Unit,
  onExportCancel: () -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  val fileKitPlatformSettings = LocalFileKitPlatformSettings.current

  ScaffoldWithTopBar(
    title = title,
    actions = {
      IconButton(
        onClick = {
          coroutineScope.launch {
            if (!FileKit.isDirectoryPickerSupported()) return@launch
            if (!FileKit.isSaveFileWithoutBytesSupported()) return@launch

            val filePath = FileKit.saveFile(
              platformSettings = fileKitPlatformSettings,
              baseName = "data",
              extension = "json",
            )?.path?.toPath() ?: return@launch

            onExportStart(filePath)
          }
        },
        enabled = uiState.tableData.isNotEmpty(),
      ) {
        Icon(SymbolsRounded.Download, null)
      }
    },
  ) { paddingValues ->
    Column(modifier = Modifier.padding(paddingValues)) {
      Table(
        modifier = Modifier.fillMaxSize().tableBorders(),
        items = uiState.tableData,
        itemKey = { _, item -> item.path },
        tableColumnInfos = uiState.tableColumnInfos,
        onTableColumnInfosChange = onTableColumnInfosChange,
        row = { _, item -> ExportTabRow(uiState.tableColumnInfos, item) },
      )
    }
  }

  when (uiState.exportProgress) {
    ExportProgress.InProgress -> ExportInProgressAlertDialog(onExportCancel)
    is ExportProgress.Error -> ExportErrorAlertDialog(onExportCancel, uiState.exportProgress)
    is ExportProgress.Success -> ExportSuccessAlertDialog(onExportCancel, uiState.exportProgress)
    ExportProgress.None -> Unit
  }
}

@Composable
private fun ExportInProgressAlertDialog(onExportCancel: () -> Unit) {
  SimpleAlertDialogDismissOnly(
    title = stringResource(Res.string.export_tab_in_progress),
    text = { LinearProgressIndicator() },
    onDismissRequest = onExportCancel,
  )
}

@Composable
private fun ExportErrorAlertDialog(
  onExportCancel: () -> Unit,
  exportProgress: ExportProgress.Error
) {
  SimpleAlertDialogDismissOnly(
    title = stringResource(Res.string.error),
    text = { SelectionContainer { Text(exportProgress.message) } },
    onDismissRequest = onExportCancel,
  )
}

@Composable
private fun ExportSuccessAlertDialog(
  onExportCancel: () -> Unit,
  exportProgress: ExportProgress.Success
) {
  SimpleAlertDialogDismissOnly(
    title = stringResource(Res.string.export_tab_exported),
    text = { Text(stringResource(Res.string.export_tab_exported_support, exportProgress.path)) },
    onDismissRequest = onExportCancel,
    buttonLabel = stringResource(Res.string.dismiss),
  )
}

@Composable
private fun ExportTabRow(tableColumnInfos: List<TableColumnInfo>, item: IndexedObject) {
  TableRow {
    TableCell(
      modifier = Modifier.width(tableColumnInfos[0].width),
      text = item.path.name,
      leadingContent = {
        FileThumbnail(
          modifier = Modifier
            .clip(RoundedCornerShape(2.dp))
            .padding(horizontal = 4.dp)
            .size(16.dp),
          item = item,
        )
      },
    )
    TableCell(
      modifier = Modifier.width(tableColumnInfos[1].width),
      text = formatBytes(item.sizeBytes),
    )
    TableCell(
      modifier = Modifier.width(tableColumnInfos[2].width),
      text = item.path.toString(),
    )
  }
}


