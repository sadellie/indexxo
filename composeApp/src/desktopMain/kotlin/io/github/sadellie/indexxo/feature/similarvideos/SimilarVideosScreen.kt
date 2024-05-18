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

package io.github.sadellie.indexxo.feature.similarvideos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.similar_videos
import io.github.sadellie.indexxo.core.common.stateIn
import io.github.sadellie.indexxo.core.data.IndexedObjectRepository
import io.github.sadellie.indexxo.core.designsystem.component.EmptyScreen
import io.github.sadellie.indexxo.core.model.MoveToTrashProgress
import io.github.sadellie.indexxo.core.model.SimilarIndexedObjectsGroup
import io.github.sadellie.indexxo.core.ui.IndexedObjectsGroupSelectorView
import io.github.sadellie.indexxo.feature.common.IndexedObjectSelectorScreenModel
import io.github.sadellie.indexxo.feature.common.IndexedObjectsGroupSelectorUIState
import io.github.sadellie.indexxo.feature.similarimages.SimilarImagesScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import okio.Path
import org.jetbrains.compose.resources.stringResource

data object SimilarVideosScreen : Screen {
  private fun readResolve(): Any = SimilarImagesScreen

  @Composable
  override fun Content() {
    val localNavigator = LocalNavigator.currentOrThrow
    val screenModel = koinScreenModel<SimilarVideosScreenModel>()
    when (val uiState = screenModel.uiState.collectAsState().value) {
      is SimilarVideosUIState -> IndexedObjectsGroupSelectorView(
        uiState = uiState,
        title = stringResource(Res.string.similar_videos),
        navigateUp = localNavigator::pop,
        onGalleryViewStateChange = screenModel::onGalleryViewStateChange,
        onSelectPath = screenModel::onSelectPath,
        onSelectPaths = screenModel::onSelectPaths,
        onMoveToTrashStart = screenModel::moveToTrashStart,
        onMoveToTrashCancel = screenModel::moveToTrashCancel,
        onDiscard = screenModel::onDiscard,
      )

      else -> EmptyScreen()
    }
  }
}

data class SimilarVideosUIState(
  override val selectedPaths: Set<Path>,
  override val indexedObjectsGroups: List<SimilarIndexedObjectsGroup>,
  override val moveToTrashProgress: MoveToTrashProgress?,
  override val isGalleryViewEnabled: Boolean
) : IndexedObjectsGroupSelectorUIState<SimilarIndexedObjectsGroup>

class SimilarVideosScreenModel(
  indexedObjectRepository: IndexedObjectRepository,
) : IndexedObjectSelectorScreenModel(indexedObjectRepository) {
  override val isGalleryViewEnabled = MutableStateFlow(true)
  override val uiState = combine(
    indexedObjectRepository.similarVideos,
    selectedPaths,
    moveToTrashProgress,
    isGalleryViewEnabled,
  ) { indexedObjectsGroups, paths, moveToTrashProgress, isGalleryViewEnabled ->
    return@combine SimilarVideosUIState(
      selectedPaths = paths,
      moveToTrashProgress = moveToTrashProgress,
      indexedObjectsGroups = indexedObjectsGroups,
      isGalleryViewEnabled = isGalleryViewEnabled,
    )
  }
    .stateIn(screenModelScope, null)

  override fun onDiscardSelected() {
    screenModelScope.launch {
      indexedObjectRepository.discardSimilarVideos(selectedPaths.value)
    }
  }
}
