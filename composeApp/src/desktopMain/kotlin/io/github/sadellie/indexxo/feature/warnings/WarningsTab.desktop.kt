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

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import io.github.sadellie.indexxo.core.common.stateIn
import io.github.sadellie.indexxo.core.data.IndexedObjectRepository
import io.github.sadellie.indexxo.core.designsystem.theme.Previewer
import io.github.sadellie.indexxo.core.model.Warning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.Path.Companion.toPath

@Composable
actual fun Screen.getWarningsTabModel() = koinScreenModel<WarningsTabModel>()

actual class WarningsTabModel(private val indexedObjectRepository: IndexedObjectRepository) : ScreenModel {
  private val _warnings = indexedObjectRepository.warnings
  private val _alertDialogWarning = MutableStateFlow<Warning?>(null)
  actual val uiState =
    combine(_warnings, _alertDialogWarning) { warnings, alertDialogWarning ->
      WarningsTabUIState(warnings, alertDialogWarning)
    }
      .stateIn(screenModelScope, null)

  actual fun onDismissWarning(warning: Warning) {
    screenModelScope.launch {
      indexedObjectRepository.discardWarning(warning)
    }
  }

  actual fun onAlertDialogWarning(warning: Warning?) =
    _alertDialogWarning.update { warning }
}

@Composable
@Preview
private fun PreviewWarningsTabViewEmpty() = Previewer {
  WarningsTabView(
    title = "Warnings",
    uiState = WarningsTabUIState(warnings = emptyList(), alertDialogWarning = null),
    onDismissWarning = {},
    onAlertDialogWarning = {},
  )
}

@Composable
@Preview
private fun PreviewWarningsTabView() = Previewer {
  val warnings =
    listOf(
      Warning(
        path = "brokenObject.0.txt".toPath(),
        message = null,
        stackTrace = "test"
      ),
      Warning(
        path = "brokenObject.1.txt".toPath(),
        message = "Test message",
        stackTrace = "test"
      ),
      Warning(
        path = "brokenObject.0.txt".toPath(),
        message = null,
        stackTrace = "test"
      ),
    )

  WarningsTabView(
    title = "Warnings",
    uiState = WarningsTabUIState(
      warnings = warnings,
      alertDialogWarning = null,
    ),
    onDismissWarning = {},
    onAlertDialogWarning = {},
  )
}
