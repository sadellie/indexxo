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

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import io.github.sadellie.indexxo.core.model.Warning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okio.Path.Companion.toPath

@Composable
actual fun Screen.getWarningsTabModel(): WarningsTabModel =
  rememberScreenModel { WarningsTabModel() }

actual class WarningsTabModel : ScreenModel {
  private val warnings =
    listOf(
      Warning(
        path = "brokenObject.0.txt".toPath(),
        message = null,
        stackTrace = "test",
      ),
      Warning(
        path = "brokenObject.1.txt".toPath(),
        message = "Test message",
        stackTrace = "test",
      ),
      Warning(
        path = "brokenObject.0.txt".toPath(),
        message = null,
        stackTrace = "test",
      ),
    )
  actual val uiState: StateFlow<WarningsTabUIState?> = MutableStateFlow(
    WarningsTabUIState(
      warnings = warnings,
      alertDialogWarning = null,
    ),
  )

  actual fun onDismissWarning(warning: Warning) = Unit

  actual fun onAlertDialogWarning(warning: Warning?) = Unit
}
