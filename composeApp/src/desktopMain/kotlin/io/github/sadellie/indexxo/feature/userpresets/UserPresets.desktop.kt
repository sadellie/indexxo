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

package io.github.sadellie.indexxo.feature.userpresets

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import io.github.sadellie.indexxo.core.common.stateIn
import io.github.sadellie.indexxo.core.data.PreferencesRepository
import io.github.sadellie.indexxo.core.data.UserPresetsRepository
import io.github.sadellie.indexxo.core.database.model.UserPreset
import io.github.sadellie.indexxo.core.designsystem.theme.Previewer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Composable
actual fun Screen.getUserPresetsScreenModel() = koinScreenModel<UserPresetsScreenModel>()

actual class UserPresetsScreenModel(
  private val userPresetsRepository: UserPresetsRepository,
  private val preferencesRepository: PreferencesRepository,
) : ScreenModel {
  actual val forceNavigateToSettings = MutableStateFlow(false)
  private val _dialogState = MutableStateFlow<UserPresetDialogState>(UserPresetDialogState.None)
  private val _preset = userPresetsRepository.currentUserPreset

  actual val uiState =
    combine(
      userPresetsRepository.allUserPresets,
      _dialogState,
      _preset,
    ) { allPresets, dialogState, preset ->
      return@combine UserPresetsUIState.Ready(
        presets = allPresets,
        selectedPresetId = preset?.id ?: -1,
        userPresetDialogState = dialogState,
      )
    }
      .stateIn(scope = screenModelScope, initialValue = UserPresetsUIState.Loading)

  actual fun selectUserPreset(userPreset: UserPreset) {
    screenModelScope.launch {
      preferencesRepository.updatePresetId(userPreset.id)
      forceNavigateToSettings.update { true }
    }
  }

  actual fun createUserPreset(name: String) {
    screenModelScope.launch { userPresetsRepository.createUserPreset(name) }
  }

  actual fun updateUserPreset(userPreset: UserPreset) {
    screenModelScope.launch { userPresetsRepository.updateUserPreset(userPreset) }
  }

  actual fun deleteUserPreset(userPreset: UserPreset) {
    screenModelScope.launch { userPresetsRepository.deleteUserPreset(userPreset) }
  }

  actual fun updateDialogState(dialogState: UserPresetDialogState) =
    _dialogState.update { dialogState }
}

@Composable
@Preview
private fun PreviewUserPresetsViewReady() = Previewer {
  UserPresetsView(
    uiState = UserPresetsUIState.Ready(
      presets = List(15) {
        UserPreset(
          id = 0,
          name = "Preset $it",
          maxThreads = 3,
          includedDirectories = emptyList(),
          excludedDirectories = emptyList(),
          includedFiles = emptyList(),
          excludedFiles = emptyList(),
          includedExtensions = emptyList(),
          excludedExtensions = emptyList(),
          isDuplicateHashesEnabled = true,
          isDuplicateFileNamesEnabled = true,
          isDuplicateFolderNamesEnabled = true,
          isEmptyFoldersEnabled = true,
          isEmptyFilesEnabled = false,
          isSimilarImagesEnabled = true,
          similarImagesMinSimilarity = 0.8f,
          isSimilarImagesImproveAccuracy = true,
          isSimilarVideosEnabled = true,
          similarVideosMinimalHashSimilarity = 0.5f,
          similarVideosMinimalFrameSimilarity = 0.8f,
          similarVideosFPS = 60,
        )
      },
      selectedPresetId = 3,
      userPresetDialogState = UserPresetDialogState.None,
    ),
    onSelect = {},
    onCreate = {},
    updateUserPreset = {},
    deleteUserPreset = {},
    updateDialogState = {},
  )
}
