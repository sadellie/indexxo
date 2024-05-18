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

package io.github.sadellie.indexxo.feature.loader

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import io.github.sadellie.indexxo.core.database.model.UserPreset
import io.github.sadellie.indexxo.core.model.DuplicateHashesAnalyzing
import io.github.sadellie.indexxo.core.model.DuplicateHashesComputingFullHash
import io.github.sadellie.indexxo.core.model.DuplicateNamesAnalyzing
import io.github.sadellie.indexxo.core.model.EmptyFilesAnalyzing
import io.github.sadellie.indexxo.core.model.EmptyFoldersAnalyzing
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.Indexing
import io.github.sadellie.indexxo.core.model.IndexingStage
import io.github.sadellie.indexxo.core.model.SimilarImagesComparing
import io.github.sadellie.indexxo.core.model.SimilarVideosComparing
import io.github.sadellie.indexxo.core.model.Walking
import io.github.sadellie.indexxo.fakeIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Composable
actual fun Screen.getLoaderScreenModel(userPreset: UserPreset) =
  rememberScreenModel { LoaderScreenModel() }

actual class LoaderScreenModel : ScreenModel {
  actual val forceNavigateToHome = MutableStateFlow(false)
  private val _uiState = MutableStateFlow<LoaderScreenUIState>(LoaderScreenUIState.NotStarted)
  actual val uiState: StateFlow<LoaderScreenUIState> = _uiState.asStateFlow()

  init {
    screenModelScope.launch {
      val folders = fakeIndex.filter { it.fileCategory == FileCategory.FOLDER }
      val notFolders = fakeIndex.filter { it.fileCategory != FileCategory.FOLDER }
      val images = fakeIndex.filter { it.fileCategory == FileCategory.IMAGE }
      val videos = fakeIndex.filter { it.fileCategory == FileCategory.VIDEO }

      folders.forEach { indexedObject ->
        delay(1)
        updateProgress(Walking(indexedObject.path))
      }

      fakeIndex.forEachIndexed { index, indexedObject ->
        delay(1)
        updateProgress(Indexing(index.toFloat() / fakeIndex.size, indexedObject))
      }

      notFolders.forEachIndexed { index, indexedObject ->
        delay(1)
        updateProgress(
          DuplicateHashesAnalyzing(index.toFloat() / notFolders.size, indexedObject),
        )
      }

      notFolders.forEachIndexed { index, indexedObject ->
        delay(1)
        updateProgress(
          DuplicateHashesComputingFullHash(index.toFloat() / notFolders.size, indexedObject),
        )
      }

      fakeIndex.forEachIndexed { index, indexedObject ->
        delay(1)
        updateProgress(
          DuplicateNamesAnalyzing(index.toFloat() / fakeIndex.size, indexedObject),
        )
      }

      notFolders.forEachIndexed { index, indexedObject ->
        delay(1)
        updateProgress(
          EmptyFilesAnalyzing(index.toFloat() / notFolders.size, indexedObject),
        )
      }

      folders.forEachIndexed { index, indexedObject ->
        delay(1)
        updateProgress(
          EmptyFoldersAnalyzing(index.toFloat() / folders.size, indexedObject),
        )
      }

      images.forEachIndexed { index, indexedObject ->
        delay(15)
        updateProgress(
          SimilarImagesComparing(index.toFloat() / images.size, indexedObject),
        )
      }

      videos.forEachIndexed { index, indexedObject ->
        delay(20)
        updateProgress(
          SimilarVideosComparing(index.toFloat() / videos.size, indexedObject),
        )
      }

      forceNavigateToHome.update { true }
    }
  }

  private fun updateProgress(stage: IndexingStage) {
    _uiState.update { LoaderScreenUIState.InProgress(stage) }
  }
}
