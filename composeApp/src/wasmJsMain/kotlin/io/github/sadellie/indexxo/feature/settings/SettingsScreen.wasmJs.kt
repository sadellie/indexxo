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

package io.github.sadellie.indexxo.feature.settings

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import io.github.sadellie.indexxo.core.common.stateIn
import io.github.sadellie.indexxo.core.database.model.UserPreset
import io.github.sadellie.indexxo.fakeUserPreset
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import okio.Path

@Composable
actual fun Screen.getSettingsScreenModel() = rememberScreenModel { SettingsScreenModel() }

actual class SettingsScreenModel : ScreenModel {
  private val _userPreset = MutableStateFlow(fakeUserPreset)

  @OptIn(ExperimentalCoroutinesApi::class)
  actual val uiState: StateFlow<SettingsUIState> =
    _userPreset.mapLatest {
      SettingsUIState.Ready(
        preset = it,
        dialogState = null,
        enableStartButton = true,
        showResumeButton = false,
        analyzersCount = 6,
      )
    }
      .stateIn(screenModelScope, SettingsUIState.Loading)

  actual fun updateUserPreset(userPreset: UserPreset) = _userPreset.update { userPreset }
  actual fun addUserPresetPath(path: Path, included: Boolean, isDirectory: Boolean, presetId: Int) =
    Unit

  actual fun editUserPresetPath(id: Int, path: Path) = Unit
  actual fun removeUserPresetPath(id: Int) = Unit
  actual fun addExtension(extension: String, included: Boolean, presetId: Int) = Unit
  actual fun editExtension(id: Int, extension: String) = Unit
  actual fun removeExtension(id: Int) = Unit
    actual fun updateDialogState(dialogState: PathsAndExtensionsDialogState?) = Unit
}
