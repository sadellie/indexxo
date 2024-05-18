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

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import co.touchlab.kermit.Logger
import io.github.sadellie.indexxo.core.common.localDateTimeNow
import io.github.sadellie.indexxo.core.common.stateIn
import io.github.sadellie.indexxo.core.data.IndexedObjectRepository
import io.github.sadellie.indexxo.core.database.model.UserPreset
import io.github.sadellie.indexxo.core.designsystem.theme.Previewer
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObjectImpl
import io.github.sadellie.indexxo.core.model.SimilarImagesComparing
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import okio.Path.Companion.toPath
import org.koin.core.parameter.parametersOf

@Composable
actual fun Screen.getLoaderScreenModel(userPreset: UserPreset) =
  koinScreenModel<LoaderScreenModel> { parametersOf(userPreset) }

actual class LoaderScreenModel(
  userPreset: UserPreset,
  indexedObjectRepository: IndexedObjectRepository,
) : ScreenModel {
  actual val forceNavigateToHome = MutableStateFlow(false)
  private val stage = indexedObjectRepository.index(userPreset)

  @OptIn(ExperimentalCoroutinesApi::class)
  actual val uiState = stage
    .mapLatest(LoaderScreenUIState::InProgress)
    .catch<LoaderScreenUIState> {
      Logger.e(TAG, it) { "Unexpected exception while indexing" }
      emit(LoaderScreenUIState.Error(it.message ?: "No error description"))
    }
    .onCompletion {
      forceNavigateToHome.update { true }
    }
    .stateIn(screenModelScope, LoaderScreenUIState.NotStarted)

  override fun onDispose() {
    screenModelScope.cancel()
    super.onDispose()
  }
}

@Composable
@Preview
private fun PreviewLoaderScreenViewError() = Previewer {
  LoaderScreenViewError(uiState = LoaderScreenUIState.Error("File doesn't exist"), onCancel = {})
}

@Preview
@Composable
private fun PreviewLoaderScreenViewInProgress() = Previewer {
  LoaderScreenViewInProgress(
    uiState =
    LoaderScreenUIState.InProgress(
      indexingStage = SimilarImagesComparing(
        value = 0.5f,
        indexedObject = IndexedObjectImpl(
          path = "name.txt".toPath(),
          parentPath = null,
          sizeBytes = 0L,
          fileCategory = FileCategory.DOCUMENT,
          createdDate = localDateTimeNow(),
          modifiedDate = localDateTimeNow(),
        ),
      ),
    ),
    onCancel = {},
  )
}

private const val TAG = "LoaderScreenModel"
