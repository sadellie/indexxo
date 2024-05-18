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

package io.github.sadellie.indexxo.feature.duplicatehashes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.duplicate_hashes
import io.github.sadellie.indexxo.core.common.formatBytes
import io.github.sadellie.indexxo.core.common.stateIn
import io.github.sadellie.indexxo.core.data.IndexedObjectRepository
import io.github.sadellie.indexxo.core.designsystem.component.EmptyScreen
import io.github.sadellie.indexxo.core.model.DuplicateHash
import io.github.sadellie.indexxo.core.model.MoveToTrashProgress
import io.github.sadellie.indexxo.core.ui.IndexedObjectsGroupSelectorView
import io.github.sadellie.indexxo.feature.common.IndexedObjectSelectorScreenModel
import io.github.sadellie.indexxo.feature.common.IndexedObjectsGroupSelectorUIState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import okio.Path
import org.jetbrains.compose.resources.stringResource

data object DuplicateHashesScreen : Screen {
  private fun readResolve(): Any = DuplicateHashesScreen

  @Composable
  override fun Content() {
    val localNavigator = LocalNavigator.currentOrThrow
    val screenModel = getDuplicateHashesScreenModel()
    when (val uiState = screenModel.uiState.collectAsState().value) {
      is DuplicateHashUIState -> IndexedObjectsGroupSelectorView(
        uiState = uiState,
        title = stringResource(Res.string.duplicate_hashes),
        navigateUp = localNavigator::pop,
        onGalleryViewStateChange = screenModel::onGalleryViewStateChange,
        onSelectPath = screenModel::onSelectPath,
        onSelectPaths = screenModel::onSelectPaths,
        onMoveToTrashStart = screenModel::moveToTrashStart,
        onMoveToTrashCancel = screenModel::moveToTrashCancel,
        onDiscard = screenModel::onDiscard,
        listHeader = DuplicateHash::listHeader,
      )

      else -> EmptyScreen()
    }
  }
}

data class DuplicateHashUIState(
  override val selectedPaths: Set<Path>,
  override val indexedObjectsGroups: List<DuplicateHash>,
  override val moveToTrashProgress: MoveToTrashProgress?,
  override val isGalleryViewEnabled: Boolean
) : IndexedObjectsGroupSelectorUIState<DuplicateHash>

@Composable
fun Screen.getDuplicateHashesScreenModel() = koinScreenModel<DuplicateHashesScreenModel>()

class DuplicateHashesScreenModel(
  indexedObjectRepository: IndexedObjectRepository,
) : IndexedObjectSelectorScreenModel(indexedObjectRepository) {
  override val uiState: StateFlow<DuplicateHashUIState?> = combine(
    indexedObjectRepository.duplicateHashes,
    selectedPaths,
    moveToTrashProgress,
    isGalleryViewEnabled,
  ) { indexedObjectsGroups, paths, moveToTrashProgress, isGalleryViewEnabled ->
    return@combine DuplicateHashUIState(
      selectedPaths = paths,
      moveToTrashProgress = moveToTrashProgress,
      indexedObjectsGroups = indexedObjectsGroups,
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

private fun DuplicateHash.listHeader(): String = "${formatBytes(totalSizeBytes)} | $hash"
