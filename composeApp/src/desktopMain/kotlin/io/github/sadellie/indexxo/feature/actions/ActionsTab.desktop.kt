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

package io.github.sadellie.indexxo.feature.actions

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import io.github.sadellie.indexxo.core.common.combine
import io.github.sadellie.indexxo.core.common.stateIn
import io.github.sadellie.indexxo.core.data.IndexedObjectRepository
import io.github.sadellie.indexxo.core.data.UserPresetsRepository
import io.github.sadellie.indexxo.core.database.model.UserPreset
import io.github.sadellie.indexxo.core.designsystem.theme.Previewer
import io.github.sadellie.indexxo.feature.duplicatefilenames.DuplicateFileNamesScreen
import io.github.sadellie.indexxo.feature.duplicatefoldernames.DuplicateFolderNamesScreen
import io.github.sadellie.indexxo.feature.duplicatehashes.DuplicateHashesScreen
import io.github.sadellie.indexxo.feature.emptyfiles.EmptyFilesScreen
import io.github.sadellie.indexxo.feature.emptyfolders.EmptyFoldersScreen
import io.github.sadellie.indexxo.feature.settings.SettingsScreen
import io.github.sadellie.indexxo.feature.similarimages.SimilarImagesScreen
import io.github.sadellie.indexxo.feature.similarvideos.SimilarVideosScreen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@Composable
actual fun Tab.ActionsTabContent() {
  val tabModel = koinScreenModel<ActionsTabModel>()
  val localNavigator = LocalNavigator.currentOrThrow
  val uiState = tabModel.uiState.collectAsState()

  ActionsTabView(
    title = options.title,
    uiState = uiState.value,
    navigateToProblemsScreen = {
      val destination = when (it) {
        ProblemTypeAction.DUPLICATE_HASHES -> DuplicateHashesScreen
        ProblemTypeAction.DUPLICATE_FILE_NAMES -> DuplicateFileNamesScreen
        ProblemTypeAction.DUPLICATE_FOLDER_NAMES -> DuplicateFolderNamesScreen
        ProblemTypeAction.EMPTY_FILES -> EmptyFilesScreen
        ProblemTypeAction.EMPTY_FOLDERS -> EmptyFoldersScreen
        ProblemTypeAction.SIMILAR_IMAGES -> SimilarImagesScreen
        ProblemTypeAction.SIMILAR_VIDEOS -> SimilarVideosScreen
      }
      localNavigator.parent?.push(destination)
    },
    navigateToSettings = { localNavigator.parent?.replaceAll(SettingsScreen) },
  )
}

@OptIn(ExperimentalCoroutinesApi::class)
class ActionsTabModel(
  userPresetsRepository: UserPresetsRepository,
  indexedObjectRepository: IndexedObjectRepository,
) : ScreenModel {
  private val _preset = userPresetsRepository.currentUserPreset

  private inline fun <reified T> Flow<T>.countIfEnabled(
    crossinline presetCheckBlock: (UserPreset) -> Boolean,
    crossinline produceSize: (T) -> Int
  ): Flow<Int> {
    return _preset.flatMapLatest { preset ->
      if (preset == null) return@flatMapLatest flowOf(0)

      if (presetCheckBlock(preset)) this.map(produceSize) else flowOf(0)
    }
  }

  private val _duplicateHashesSize = indexedObjectRepository.duplicateHashes
    .countIfEnabled(UserPreset::isDuplicateHashesEnabled) { duplicateHashes ->
      duplicateHashes.sumOf { it.duplicates.size }
    }

  private val _duplicateFileNamesSize = indexedObjectRepository.duplicateFileNames
    .countIfEnabled(UserPreset::isDuplicateFileNamesEnabled) { duplicateFileNames ->
      duplicateFileNames.sumOf { it.duplicates.size }
    }

  private val _duplicateFolderNamesSize = indexedObjectRepository.duplicateFolderNames
    .countIfEnabled(UserPreset::isDuplicateFolderNamesEnabled) { duplicateFolderNames ->
      duplicateFolderNames.sumOf { it.duplicates.size }
    }

  private val _emptyFilesSize = indexedObjectRepository.emptyFiles
    .countIfEnabled(UserPreset::isEmptyFilesEnabled) { emptyFiles -> emptyFiles.size }

  private val _emptyFoldersSize = indexedObjectRepository.emptyFolders
    .countIfEnabled(UserPreset::isEmptyFoldersEnabled) { emptyFolders -> emptyFolders.size }

  private val _similarImageGroupsSize = indexedObjectRepository.similarImages
    .countIfEnabled(UserPreset::isSimilarImagesEnabled) { similarImageGroups ->
      similarImageGroups.sumOf { it.duplicates.size }
    }

  private val _similarVideoGroupsSize = indexedObjectRepository.similarVideos
    .countIfEnabled(UserPreset::isSimilarVideosEnabled) { similarVideoGroups -> similarVideoGroups.size }

  val uiState: StateFlow<ActionsTabUIState> = combine(
    _preset,
    _duplicateHashesSize,
    _duplicateFileNamesSize,
    _duplicateFolderNamesSize,
    _emptyFilesSize,
    _emptyFoldersSize,
    _similarImageGroupsSize,
    _similarVideoGroupsSize,
  ) { preset,
      duplicateHashesSize,
      duplicateFileNamesSize,
      duplicateFolderNamesSize,
      emptyFilesSize,
      emptyFoldersSize,
      similarImageGroupsSize,
      similarVideoGroupsSize ->
    if (preset == null) return@combine ActionsTabUIState.Loading

    val totalSize = duplicateHashesSize +
      duplicateFileNamesSize +
      duplicateFolderNamesSize +
      emptyFilesSize +
      emptyFoldersSize +
      similarImageGroupsSize +
      similarVideoGroupsSize

    if (totalSize == 0) return@combine ActionsTabUIState.Empty

    return@combine ActionsTabUIState.Ready(
      duplicateHashes = duplicateHashesSize,
      duplicateFileNames = duplicateFileNamesSize,
      duplicateFolderNames = duplicateFolderNamesSize,
      emptyFiles = emptyFilesSize,
      emptyFolders = emptyFoldersSize,
      similarImages = similarImageGroupsSize,
      similarVideos = similarVideoGroupsSize,
    )

  }.stateIn(screenModelScope, ActionsTabUIState.Loading)
}

@Composable
@Preview
private fun PreviewActionsTabViewEmpty() = Previewer {
  ActionsTabView(
    title = "Actions",
    uiState = ActionsTabUIState.Empty,
    navigateToProblemsScreen = {},
    navigateToSettings = {},
  )
}

@Composable
@Preview
private fun PreviewActionsTabView() = Previewer {
  ActionsTabView(
    title = "Actions",
    uiState = ActionsTabUIState.Ready(
      duplicateHashes = 234,
      duplicateFileNames = 423,
      duplicateFolderNames = 76,
      emptyFolders = 78,
      emptyFiles = 63,
      similarImages = 35,
      similarVideos = 26,
    ),
    navigateToProblemsScreen = {},
    navigateToSettings = {},
  )
}
