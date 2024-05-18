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

package io.github.sadellie.indexxo.feature.common

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.sadellie.indexxo.core.data.IndexedObjectRepository
import io.github.sadellie.indexxo.core.model.IndexedObject
import io.github.sadellie.indexxo.core.model.IndexedObjectsGroup
import io.github.sadellie.indexxo.core.model.MoveToTrashProgress
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.Path

interface IndexedObjectSelectorUIState {
  val selectedPaths: Set<Path>
  val moveToTrashProgress: MoveToTrashProgress?
  val isGalleryViewEnabled: Boolean
}

data class IndexedObjectsFlatSelectorUIState(
  val indexedObjects: List<IndexedObject>,
  override val selectedPaths: Set<Path>,
  override val moveToTrashProgress: MoveToTrashProgress?,
  override val isGalleryViewEnabled: Boolean
) : IndexedObjectSelectorUIState

interface IndexedObjectsGroupSelectorUIState<T : IndexedObjectsGroup> :
  IndexedObjectSelectorUIState {
  val indexedObjectsGroups: List<T>
}

abstract class IndexedObjectSelectorScreenModel(
  val indexedObjectRepository: IndexedObjectRepository
) : ScreenModel {
  private var job: Job? = null
  val selectedPaths = MutableStateFlow(emptySet<Path>())
  val moveToTrashProgress = MutableStateFlow<MoveToTrashProgress?>(null)
  // Open so that screen can declare gallery view as default one
  open val isGalleryViewEnabled = MutableStateFlow(false)
  abstract val uiState: StateFlow<IndexedObjectSelectorUIState?>
  abstract fun onDiscardSelected()

  fun onDiscard() {
    onDiscardSelected()
    selectedPaths.update { emptySet() }
  }

  fun moveToTrashStart() {
    job?.cancel()
    job = screenModelScope.launch {
      indexedObjectRepository.moveToTrash(selectedPaths.value) { path ->
        moveToTrashProgress.update { MoveToTrashProgress.InProgress(path) }
      }
      syncIndex()
    }
  }

  fun moveToTrashCancel() {
    job?.cancel()
    selectedPaths.update { emptySet() }
    syncIndex()
  }

  fun onSelectPath(path: Path) {
    selectedPaths.update {currentSelectPaths ->
      if (path in currentSelectPaths) {
        currentSelectPaths - path
      } else {
        currentSelectPaths + path
      }
    }
  }

  fun onSelectPaths(paths: Set<Path>) {
    selectedPaths.update {  currentSelectPaths ->
      if (paths.all { path -> path in currentSelectPaths }) {
        currentSelectPaths.minus(paths)
      } else {
        currentSelectPaths.plus(paths)
      }
    }
  }

  fun onGalleryViewStateChange(value: Boolean) {
    isGalleryViewEnabled.update { value }
  }

  private fun syncIndex() {
    screenModelScope.launch {
      moveToTrashProgress.update { MoveToTrashProgress.RefreshingIndex }
      indexedObjectRepository.syncIndexes()
      moveToTrashProgress.update { null }
    }
  }
}
