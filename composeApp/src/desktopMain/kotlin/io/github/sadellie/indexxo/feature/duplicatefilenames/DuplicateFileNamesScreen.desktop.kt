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

package io.github.sadellie.indexxo.feature.duplicatefilenames

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.duplicate_file_names
import io.github.sadellie.indexxo.core.common.formatBytes
import io.github.sadellie.indexxo.core.common.stateIn
import io.github.sadellie.indexxo.core.data.IndexedObjectRepository
import io.github.sadellie.indexxo.core.designsystem.component.EmptyScreen
import io.github.sadellie.indexxo.core.model.DuplicateName
import io.github.sadellie.indexxo.core.model.MoveToTrashProgress
import io.github.sadellie.indexxo.core.ui.IndexedObjectsGroupSelectorView
import io.github.sadellie.indexxo.feature.common.IndexedObjectSelectorScreenModel
import io.github.sadellie.indexxo.feature.common.IndexedObjectsGroupSelectorUIState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import okio.Path
import org.jetbrains.compose.resources.stringResource

data object DuplicateFileNamesScreen: Screen {
  private fun readResolve(): Any = DuplicateFileNamesScreen

  @Composable
  override fun Content() {
    val localNavigator = LocalNavigator.currentOrThrow
    val screenModel = getDuplicateFileNamesScreenModel()
    when (val uiState = screenModel.uiState.collectAsState().value) {
      is DuplicateNameUIState -> IndexedObjectsGroupSelectorView(
        uiState = uiState,
        title = stringResource(Res.string.duplicate_file_names),
        navigateUp = localNavigator::pop,
        onGalleryViewStateChange = screenModel::onGalleryViewStateChange,
        onSelectPath = screenModel::onSelectPath,
        onSelectPaths = screenModel::onSelectPaths,
        onMoveToTrashStart = screenModel::moveToTrashStart,
        onMoveToTrashCancel = screenModel::moveToTrashCancel,
        onDiscard = screenModel::onDiscard,
        listHeader = DuplicateName::listHeader,
      )

      else -> EmptyScreen()
    }
  }
}

data class DuplicateNameUIState(
  override val selectedPaths: Set<Path>,
  override val indexedObjectsGroups: List<DuplicateName>,
  override val moveToTrashProgress: MoveToTrashProgress?,
  override val isGalleryViewEnabled: Boolean
) : IndexedObjectsGroupSelectorUIState<DuplicateName>

@Composable
fun Screen.getDuplicateFileNamesScreenModel() =
  koinScreenModel<DuplicateFileNamesScreenModel>()

class DuplicateFileNamesScreenModel(
  indexedObjectRepository: IndexedObjectRepository,
) : IndexedObjectSelectorScreenModel(indexedObjectRepository) {
  override val uiState: StateFlow<DuplicateNameUIState?> = combine(
    indexedObjectRepository.duplicateFileNames,
    selectedPaths,
    moveToTrashProgress,
    isGalleryViewEnabled,
  ) { indexedObjectsGroups, paths, moveToTrashProgress, isGalleryViewEnabled ->
    return@combine DuplicateNameUIState(
      selectedPaths = paths,
      moveToTrashProgress = moveToTrashProgress,
      indexedObjectsGroups = indexedObjectsGroups,
      isGalleryViewEnabled = isGalleryViewEnabled,
    )
  }
    .stateIn(screenModelScope, null)

  override fun onDiscardSelected() {
    screenModelScope.launch {
      indexedObjectRepository.discardDuplicateFileNames(selectedPaths.value)
    }
  }
}

private fun DuplicateName.listHeader(): String = "${formatBytes(totalSizeBytes)} | $name"
