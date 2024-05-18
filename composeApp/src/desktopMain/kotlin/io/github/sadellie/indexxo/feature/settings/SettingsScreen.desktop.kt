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

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import io.github.sadellie.indexxo.core.common.stateIn
import io.github.sadellie.indexxo.core.data.IndexedObjectRepository
import io.github.sadellie.indexxo.core.data.UserPresetsRepository
import io.github.sadellie.indexxo.core.database.model.UserPreset
import io.github.sadellie.indexxo.core.database.model.UserPresetExtensionEntity
import io.github.sadellie.indexxo.core.database.model.UserPresetPathEntity
import io.github.sadellie.indexxo.core.designsystem.theme.Previewer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.Path
import okio.Path.Companion.toPath

@Composable
actual fun Screen.getSettingsScreenModel() = koinScreenModel<SettingsScreenModel>()

actual class SettingsScreenModel(
  private val userPresetsRepository: UserPresetsRepository,
  indexedObjectRepository: IndexedObjectRepository
) : ScreenModel {
  private val _dialogState = MutableStateFlow<PathsAndExtensionsDialogState?>(null)
  private val _preset = userPresetsRepository.currentUserPreset

  @OptIn(ExperimentalCoroutinesApi::class)
  private val _showResumeButton = indexedObjectRepository.indexedObjects
    .mapLatest { it.isNotEmpty() }

  actual val uiState = combine(
    _preset,
    _dialogState,
    _showResumeButton,
  ) { preset, dialogState, showResumeButton ->
    if (preset == null) return@combine SettingsUIState.PresetNotFound

    var analyzersCount = 0
    if (preset.isDuplicateHashesEnabled) analyzersCount++
    if (preset.isDuplicateFileNamesEnabled) analyzersCount++
    if (preset.isDuplicateFolderNamesEnabled) analyzersCount++
    if (preset.isEmptyFoldersEnabled) analyzersCount++
    if (preset.isEmptyFilesEnabled) analyzersCount++
    if (preset.isSimilarImagesEnabled) analyzersCount++
    if (preset.isSimilarVideosEnabled) analyzersCount++

    SettingsUIState.Ready(
      preset = preset,
      dialogState = dialogState,
      enableStartButton = (preset.includedDirectories + preset.includedFiles).isNotEmpty(),
      showResumeButton = showResumeButton,
      analyzersCount = analyzersCount,
    )
  }
    .stateIn(screenModelScope, SettingsUIState.Loading)

  actual fun updateUserPreset(userPreset: UserPreset) {
    screenModelScope.launch { userPresetsRepository.updateUserPreset(userPreset) }
  }

  actual fun addUserPresetPath(
    path: Path,
    included: Boolean,
    isDirectory: Boolean,
    presetId: Int
  ) {
    screenModelScope.launch {
      userPresetsRepository.insertUserPresetPath(
        path = path,
        isIncluded = included,
        isDirectory = isDirectory,
        basePresetId = presetId,
      )
    }
  }

  actual fun editUserPresetPath(
    id: Int,
    path: Path
  ) {
    screenModelScope.launch { userPresetsRepository.updateUserPresetPath(id, path) }
  }

  actual fun removeUserPresetPath(id: Int) {
    screenModelScope.launch { userPresetsRepository.deleteUserPresetPath(id) }
  }

  actual fun addExtension(
    extension: String,
    included: Boolean,
    presetId: Int
  ) {
    screenModelScope.launch {
      userPresetsRepository.addExtension(
        extension = extension,
        included = included,
        basePresetId = presetId,
      )
    }
  }

  actual fun editExtension(
    id: Int,
    extension: String
  ) {
    screenModelScope.launch { userPresetsRepository.editExtension(id, extension) }
  }

  actual fun removeExtension(id: Int) {
    screenModelScope.launch { userPresetsRepository.removeExtension(id) }
  }

  actual fun updateDialogState(dialogState: PathsAndExtensionsDialogState?) =
    _dialogState.update { dialogState }
}

@Composable
@Preview
private fun PreviewSettingsViewReady() = Previewer {
  SettingsViewReady(
    uiState = SettingsUIState.Ready(
      preset = UserPreset(
        id = 0,
        name = "Test",
        maxThreads = 3,
        includedDirectories = listOf(
          UserPresetPathEntity(
            path = "/directory".toPath(),
            isIncluded = true,
            isDirectory = true,
            basePresetId = 0,
          ),
        ),
        excludedDirectories = emptyList(),
        includedFiles = listOf(
          UserPresetPathEntity(
            path = "/directory/file.ext".toPath(),
            isIncluded = true,
            isDirectory = true,
            basePresetId = 0,
          ),
        ),
        excludedFiles = emptyList(),
        includedExtensions = List(3) {
          UserPresetExtensionEntity(
            id = it,
            extension = "ext$it",
            basePresetId = 0,
            isIncluded = false,
          )
        },
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
      ),
      dialogState = null,
      enableStartButton = true,
      showResumeButton = false,
      analyzersCount = 3,
    ),
    addUserPresetPath = { _, _, _, _ -> },
    editUserPresetPath = { _, _ -> },
    removeUserPresetPath = {},
    addExtension = { _, _, _ -> },
    editExtension = { _, _ -> },
    removeExtension = {},
    updateDialogState = {},
    updateUserPreset = {},
    onPresetClick = {},
    onAnalyzersSettingsClick = {},
    onResume = {},
    onStart = {},
  )
}

@Composable
@Preview
private fun PreviewSettingsViewReadyEmpty() = Previewer {
  SettingsViewReady(
    uiState = SettingsUIState.Ready(
      preset = UserPreset(
        id = 0,
        name = "Test",
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
      ),
      dialogState = null,
      enableStartButton = true,
      showResumeButton = true,
      analyzersCount = 0,
    ),
    addUserPresetPath = { _, _, _, _ -> },
    editUserPresetPath = { _, _ -> },
    removeUserPresetPath = {},
    addExtension = { _, _, _ -> },
    editExtension = { _, _ -> },
    removeExtension = {},
    updateDialogState = {},
    updateUserPreset = {},
    onPresetClick = {},
    onAnalyzersSettingsClick = {},
    onResume = {},
    onStart = {},
  )
}
