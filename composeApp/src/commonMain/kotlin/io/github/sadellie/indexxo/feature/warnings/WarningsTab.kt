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

package io.github.sadellie.indexxo.feature.warnings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.dismiss
import indexxo.composeapp.generated.resources.warnings_tab_placeholder
import indexxo.composeapp.generated.resources.warnings_tab_stack_trace
import indexxo.composeapp.generated.resources.warnings_tab_title
import io.github.sadellie.indexxo.core.designsystem.component.EmptyScreen
import io.github.sadellie.indexxo.core.designsystem.component.RowWithScrollbar
import io.github.sadellie.indexxo.core.designsystem.component.ScaffoldWithTopBar
import io.github.sadellie.indexxo.core.designsystem.component.SimpleAlertDialogDismissOnly
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Check
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Checklist
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded
import io.github.sadellie.indexxo.core.model.Warning
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.stringResource

data object WarningsTab : Tab {

  @Composable
  override fun Content() {
    val screenModel = getWarningsTabModel()

    when (val uiState = screenModel.uiState.collectAsState().value) {
      null -> EmptyScreen()
      else ->
        WarningsTabView(
          title = options.title,
          uiState = uiState,
          onDismissWarning = screenModel::onDismissWarning,
          onAlertDialogWarning = screenModel::onAlertDialogWarning,
        )
    }
  }

  override val options: TabOptions
    @Composable
    get() {
      val title = stringResource(Res.string.warnings_tab_title)
      val icon = rememberVectorPainter(SymbolsRounded.Checklist)

      return remember(title) { TabOptions(index = 5u, title = title, icon = icon) }
    }
}

@Composable expect fun Screen.getWarningsTabModel(): WarningsTabModel

expect class WarningsTabModel : ScreenModel {
  val uiState: StateFlow<WarningsTabUIState?>

  fun onDismissWarning(warning: Warning)

  fun onAlertDialogWarning(warning: Warning?)
}

data class WarningsTabUIState(
  val warnings: List<Warning>,
  val alertDialogWarning: Warning?,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarningsTabView(
  title: String,
  uiState: WarningsTabUIState,
  onDismissWarning: (Warning) -> Unit,
  onAlertDialogWarning: (Warning?) -> Unit,
) {
  ScaffoldWithTopBar(title = title) { paddingValues ->
    if (uiState.warnings.isEmpty()) {
      Placeholder(Modifier.padding(paddingValues))
      return@ScaffoldWithTopBar
    }

    val listState = rememberLazyListState()

    RowWithScrollbar(
      modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp),
      listState = listState,
    ) {
      LazyColumn(
        modifier = Modifier.weight(1f),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        items(uiState.warnings) { warning ->
          WarningCard(
            modifier = Modifier.fillMaxWidth(),
            warning = warning,
            onDismiss = { onDismissWarning(warning) },
            onShowStackTrace = { onAlertDialogWarning(warning) },
          )
        }
      }
    }

    if (uiState.alertDialogWarning != null) {
      StacktraceDialog(
        title = uiState.alertDialogWarning.path.name,
        stacktrace = uiState.alertDialogWarning.stackTrace ?: "",
        onAlertDialogWarning = { onAlertDialogWarning(null) },
      )
    }
  }
}

@Composable
private fun StacktraceDialog(
  title: String,
  stacktrace: String,
  onAlertDialogWarning: () -> Unit,
) {
  SimpleAlertDialogDismissOnly(
    title = title,
    text = {
      SelectionContainer(Modifier.verticalScroll(rememberScrollState())) { Text(stacktrace) }
    },
    onDismissRequest = onAlertDialogWarning,
    buttonLabel = stringResource(Res.string.dismiss),
  )
}

@Composable
private fun Placeholder(modifier: Modifier) {
  Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
  ) {
    Icon(
      imageVector = SymbolsRounded.Check,
      contentDescription = null,
      modifier = Modifier.size(48.dp),
    )
    Text(
      text = stringResource(Res.string.warnings_tab_placeholder),
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.bodyLarge,
    )
  }
}

@Composable
private fun WarningCard(
  modifier: Modifier = Modifier,
  warning: Warning,
  onDismiss: () -> Unit,
  onShowStackTrace: () -> Unit,
) {
  OutlinedCard(modifier = modifier) {
    Text(
      text = warning.path.name,
      style = MaterialTheme.typography.titleMedium,
      modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp),
    )
    SelectionContainer {
      Column {
        Text(
          text = warning.path.toString(),
          style = MaterialTheme.typography.labelMedium,
          modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp),
        )

        warning.message?.let { message ->
          Text(message, Modifier.padding(12.dp, 4.dp))
        }
      }
    }

    Row(
      modifier = Modifier.align(Alignment.End).padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
    ) {
      if (warning.stackTrace != null) {
        TextButton(onShowStackTrace) { Text(stringResource(Res.string.warnings_tab_stack_trace)) }
      }
      TextButton(onDismiss) { Text(stringResource(Res.string.dismiss)) }
    }
  }
}
