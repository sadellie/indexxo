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

package io.github.sadellie.indexxo.feature.emptyfiles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.empty_files
import io.github.sadellie.indexxo.core.common.stateIn
import io.github.sadellie.indexxo.core.data.IndexedObjectRepository
import io.github.sadellie.indexxo.core.designsystem.component.EmptyScreen
import io.github.sadellie.indexxo.core.ui.IndexedObjectsFlatSelectorView
import io.github.sadellie.indexxo.feature.common.IndexedObjectSelectorScreenModel
import io.github.sadellie.indexxo.feature.common.IndexedObjectsFlatSelectorUIState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

data object EmptyFilesScreen : Screen {
  private fun readResolve(): Any = EmptyFilesScreen

  @Composable
  override fun Content() {
    val localNavigator = LocalNavigator.currentOrThrow
    val screenModel = koinScreenModel<EmptyFilesScreenModel>()
    when (val uiState = screenModel.uiState.collectAsState().value) {
      is IndexedObjectsFlatSelectorUIState -> IndexedObjectsFlatSelectorView(
        uiState = uiState,
        title = stringResource(Res.string.empty_files),
        navigateUp = localNavigator::pop,
        onGalleryViewStateChange = screenModel::onGalleryViewStateChange,
        onSelectPath = screenModel::onSelectPath,
        onMoveToTrashStart = screenModel::moveToTrashStart,
        onMoveToTrashCancel = screenModel::moveToTrashCancel,
        onDiscard = screenModel::onDiscard,
      )

      else -> EmptyScreen()
    }
  }
}

class EmptyFilesScreenModel(
  indexedObjectRepository: IndexedObjectRepository,
) : IndexedObjectSelectorScreenModel(indexedObjectRepository) {
  override val uiState: StateFlow<IndexedObjectsFlatSelectorUIState?> = combine(
    indexedObjectRepository.emptyFiles,
    selectedPaths,
    moveToTrashProgress,
    isGalleryViewEnabled,
  ) { indexedObjects, paths, moveToTrashProgress, isGalleryViewEnabled ->
    return@combine IndexedObjectsFlatSelectorUIState(
      selectedPaths = paths,
      moveToTrashProgress = moveToTrashProgress,
      indexedObjects = indexedObjects,
      isGalleryViewEnabled = isGalleryViewEnabled,
    )
  }
    .stateIn(screenModelScope, null)

  override fun onDiscardSelected() {
    screenModelScope.launch {
      indexedObjectRepository.discardDuplicateHashes(selectedPaths.value)
    }
  }
}
