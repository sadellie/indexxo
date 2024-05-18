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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.cancel
import indexxo.composeapp.generated.resources.loader_cancel_indexing_title
import indexxo.composeapp.generated.resources.resume
import io.github.sadellie.indexxo.core.database.model.UserPreset
import io.github.sadellie.indexxo.core.designsystem.LocalWindowFocusRequester
import io.github.sadellie.indexxo.core.designsystem.component.EmptyScreen
import io.github.sadellie.indexxo.core.designsystem.component.SimpleAlertDialogConfirmation
import io.github.sadellie.indexxo.core.model.IndexingStage
import io.github.sadellie.indexxo.feature.home.HomeScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.stringResource

data class LoaderScreen(val userPreset: UserPreset) : Screen {
  @Composable
  override fun Content() {
    val localNavigator = LocalNavigator.currentOrThrow
    val screenModel = getLoaderScreenModel(userPreset)
    val uiState = screenModel.uiState.collectAsState()
    val forceNavigateToHome = screenModel.forceNavigateToHome.collectAsState()
    val localWindowFocusRequester = LocalWindowFocusRequester.current

    LaunchedEffect(forceNavigateToHome.value) {
      if (forceNavigateToHome.value) {
        localWindowFocusRequester.requestFocus()
        localNavigator.replaceAll(HomeScreen)
      }
    }

    when (val uiStateValue = uiState.value) {
      is LoaderScreenUIState.InProgress ->
        LoaderScreenViewInProgress(uiState = uiStateValue, onCancel = localNavigator::pop)

      is LoaderScreenUIState.Error ->
        LoaderScreenViewError(uiState = uiStateValue, onCancel = localNavigator::pop)

      LoaderScreenUIState.NotStarted -> EmptyScreen()
    }
  }
}

@Composable
expect fun Screen.getLoaderScreenModel(userPreset: UserPreset): LoaderScreenModel

expect class LoaderScreenModel : ScreenModel {
  val forceNavigateToHome: MutableStateFlow<Boolean>
  val uiState: StateFlow<LoaderScreenUIState>
}

sealed class LoaderScreenUIState {
  data class InProgress(val indexingStage: IndexingStage) : LoaderScreenUIState()

  data class Error(val message: String) : LoaderScreenUIState()

  data object NotStarted : LoaderScreenUIState()
}

@Composable
fun LoaderScreenViewInProgress(uiState: LoaderScreenUIState.InProgress, onCancel: () -> Unit) =
  Scaffold {
    Column(
      modifier = Modifier.fillMaxSize().padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = stringResource(uiState.indexingStage.nameRes),
        style = MaterialTheme.typography.displaySmall,
        textAlign = TextAlign.Center,
      )

      if (uiState.indexingStage is IndexingStage.WithValue) {
        LinearProgressIndicator(
          modifier = Modifier.fillMaxWidth(),
          progress = { uiState.indexingStage.value },
        )
      } else {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
      }

      val text =
        remember(uiState.indexingStage) {
          when (uiState.indexingStage) {
            is IndexingStage.WithPath -> uiState.indexingStage.path.name
            is IndexingStage.WithIndexedObject -> uiState.indexingStage.indexedObject.path.name
            else -> null
          }
        }

      if (text != null) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
      }

      var showConfirmationDialog by remember { mutableStateOf(false) }
      Button({ showConfirmationDialog = true }) { Text(stringResource(Res.string.cancel)) }

      if (showConfirmationDialog) {
        SimpleAlertDialogConfirmation(
          title = stringResource(Res.string.loader_cancel_indexing_title),
          onConfirm = onCancel,
          onDismissRequest = { showConfirmationDialog = false },
          confirmLabel = stringResource(Res.string.cancel),
          dismissLabel = stringResource(Res.string.resume),
        )
      }
    }
  }

@Composable
fun LoaderScreenViewError(uiState: LoaderScreenUIState.Error, onCancel: () -> Unit) {
  Scaffold {
    Column(
      modifier = Modifier.fillMaxSize().padding(16.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = uiState.message,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.error,
      )
      Button(onCancel) { Text(stringResource(Res.string.cancel)) }
    }
  }
}
