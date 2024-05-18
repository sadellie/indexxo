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

package io.github.sadellie.indexxo.feature.userpresets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.create_new
import indexxo.composeapp.generated.resources.delete
import indexxo.composeapp.generated.resources.name
import indexxo.composeapp.generated.resources.rename
import indexxo.composeapp.generated.resources.user_presets_delete_confirmation
import indexxo.composeapp.generated.resources.user_presets_footer
import indexxo.composeapp.generated.resources.user_presets_title
import io.github.sadellie.indexxo.core.database.model.UserPreset
import io.github.sadellie.indexxo.core.designsystem.LocalWindowSizeClass
import io.github.sadellie.indexxo.core.designsystem.component.EmptyScreen
import io.github.sadellie.indexxo.core.designsystem.component.ListItem
import io.github.sadellie.indexxo.core.designsystem.component.OptionsMenuButton
import io.github.sadellie.indexxo.core.designsystem.component.OptionsMenuItem
import io.github.sadellie.indexxo.core.designsystem.component.RowWithScrollbar
import io.github.sadellie.indexxo.core.designsystem.component.ScaffoldWithTopBar
import io.github.sadellie.indexxo.core.designsystem.component.SimpleAlertDialogConfirmation
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Add
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded
import io.github.sadellie.indexxo.feature.settings.SettingsScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.stringResource

data object UserPresetsScreen : Screen {
  @Composable
  override fun Content() {
    val localNavigator = LocalNavigator.currentOrThrow
    val screenModel = getUserPresetsScreenModel()
    val uiState = screenModel.uiState.collectAsState()
    val forceNavigateToSettings = screenModel.forceNavigateToSettings.collectAsState()

    LaunchedEffect(forceNavigateToSettings.value) {
      if (forceNavigateToSettings.value) {
        localNavigator.replaceAll(SettingsScreen)
      }
    }

    UserPresetsView(
      uiState = uiState.value,
      updateUserPreset = screenModel::updateUserPreset,
      onSelect = screenModel::selectUserPreset,
      onCreate = screenModel::createUserPreset,
      deleteUserPreset = screenModel::deleteUserPreset,
      updateDialogState = screenModel::updateDialogState,
    )
  }
}

@Composable
expect fun Screen.getUserPresetsScreenModel(): UserPresetsScreenModel

expect class UserPresetsScreenModel : ScreenModel {
  val forceNavigateToSettings: MutableStateFlow<Boolean>
  val uiState: StateFlow<UserPresetsUIState>
  fun selectUserPreset(userPreset: UserPreset)
  fun createUserPreset(name: String)
  fun updateUserPreset(userPreset: UserPreset)
  fun deleteUserPreset(userPreset: UserPreset)
  fun updateDialogState(dialogState: UserPresetDialogState)
}

sealed class UserPresetsUIState {
  data object Loading : UserPresetsUIState()

  data class Ready(
    val presets: List<UserPreset>,
    val selectedPresetId: Int,
    val userPresetDialogState: UserPresetDialogState,
  ) : UserPresetsUIState()
}

sealed class UserPresetDialogState {
  data object None : UserPresetDialogState()

  data object Create : UserPresetDialogState()

  data class Rename(val userPreset: UserPreset) : UserPresetDialogState()

  data class Delete(val userPreset: UserPreset) : UserPresetDialogState()
}

@Composable
fun UserPresetsView(
  uiState: UserPresetsUIState,
  updateUserPreset: (UserPreset) -> Unit,
  onSelect: (UserPreset) -> Unit,
  onCreate: (String) -> Unit,
  deleteUserPreset: (UserPreset) -> Unit,
  updateDialogState: (UserPresetDialogState) -> Unit,
) {
  when (uiState) {
    UserPresetsUIState.Loading -> EmptyScreen()
    is UserPresetsUIState.Ready ->
      UserPresetsViewReady(
        uiState = uiState,
        onSelect = onSelect,
        onCreate = onCreate,
        updateUserPreset = updateUserPreset,
        deleteUserPreset = deleteUserPreset,
        updateDialogState = updateDialogState,
      )
  }
}

@Composable
private fun UserPresetsViewReady(
  uiState: UserPresetsUIState.Ready,
  onSelect: (UserPreset) -> Unit,
  onCreate: (String) -> Unit,
  updateUserPreset: (UserPreset) -> Unit,
  deleteUserPreset: (UserPreset) -> Unit,
  updateDialogState: (UserPresetDialogState) -> Unit,
) {
  UserPresetsLayout { paddingValues ->
    Column(
      modifier = Modifier.padding(paddingValues),
    ) {
      val presetsListModifier =
        if (LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact) {
          Modifier
        } else {
          val mask = RoundedCornerShape(PresetViewReadyCornerRadius)
          Modifier.padding(8.dp).clip(mask).background(MaterialTheme.colorScheme.background)
        }

      PresetsList(
        modifier = presetsListModifier.weight(1f),
        selectedPresetId = uiState.selectedPresetId,
        presets = uiState.presets,
        onSelect = onSelect,
        onRenameOption = { updateDialogState(UserPresetDialogState.Rename(it)) },
        onDeleteOption = { updateDialogState(UserPresetDialogState.Delete(it)) },
        onCreate = { updateDialogState(UserPresetDialogState.Create) },
      )
      Text(
        text = stringResource(Res.string.user_presets_footer),
        style = MaterialTheme.typography.labelMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(16.dp),
      )
    }
  }

  when (uiState.userPresetDialogState) {
    UserPresetDialogState.Create -> {
      UserPresetNameDialog(
        name = "",
        title = stringResource(Res.string.create_new),
        onConfirm = { name ->
          onCreate(name)
          updateDialogState(UserPresetDialogState.None)
        },
        onDismissRequest = { updateDialogState(UserPresetDialogState.None) },
      )
    }

    is UserPresetDialogState.Rename -> {
      UserPresetNameDialog(
        name = uiState.userPresetDialogState.userPreset.name,
        title = stringResource(Res.string.rename),
        onConfirm = { newName ->
          updateUserPreset(uiState.userPresetDialogState.userPreset.copy(name = newName))
          updateDialogState(UserPresetDialogState.None)
        },
        onDismissRequest = { updateDialogState(UserPresetDialogState.None) },
      )
    }

    is UserPresetDialogState.Delete -> {
      UserPresetDeleteDialog(
        userPreset = uiState.userPresetDialogState.userPreset,
        onConfirm = {
          deleteUserPreset(uiState.userPresetDialogState.userPreset)
          updateDialogState(UserPresetDialogState.None)
        },
        onDismissRequest = { updateDialogState(UserPresetDialogState.None) },
      )
    }

    UserPresetDialogState.None -> Unit
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserPresetsLayout(
  content: @Composable (paddingValues: PaddingValues) -> Unit
) {
  val isCompactWidth = LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact
  val mask = RoundedCornerShape(PresetViewReadyCornerRadius)
  val scaffoldModifier = if (isCompactWidth) {
    Modifier
  } else {
    Modifier.padding(72.dp).clip(mask)
  }
  val containerColor = if (isCompactWidth) {
    MaterialTheme.colorScheme.background
  } else {
    MaterialTheme.colorScheme.surfaceContainer
  }
  ScaffoldWithTopBar(
    modifier = scaffoldModifier,
    containerColor = containerColor,
    title = stringResource(Res.string.user_presets_title),
    colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = containerColor),
    content = content,
  )
}

@Composable
private fun PresetsList(
  modifier: Modifier,
  presets: List<UserPreset>,
  selectedPresetId: Int,
  onSelect: (UserPreset) -> Unit,
  onRenameOption: (UserPreset) -> Unit,
  onDeleteOption: (UserPreset) -> Unit,
  onCreate: () -> Unit,
) {
  val listState = rememberLazyListState()
  RowWithScrollbar(modifier = modifier, listState = listState) {
    var expandOptionsMenuKey by remember { mutableStateOf<UserPreset?>(null) }

    LazyColumn(modifier = Modifier.weight(1f), state = listState) {
      items(presets) { preset ->
        ListItem(
          headlineText = preset.name,
          onSelect = { onSelect(preset) },
          selected = preset.id == selectedPresetId,
          trailingContent = {
            OptionsMenuButton(
              expanded = expandOptionsMenuKey == preset,
              onExpand = { expandOptionsMenuKey = preset },
              onCollapse = { expandOptionsMenuKey = null },
            ) {
              OptionsMenuItem(
                label = stringResource(Res.string.rename),
                onClick = {
                  onRenameOption(preset)
                  expandOptionsMenuKey = null
                },
              )

              OptionsMenuItem(
                label = stringResource(Res.string.delete),
                onClick = {
                  onDeleteOption(preset)
                  expandOptionsMenuKey = null
                },
              )
            }
          },
        )
      }

      item {
        ListItem(
          headlineContent = { Text(stringResource(Res.string.create_new)) },
          modifier = Modifier.clickable { onCreate() },
          leadingContent = { Icon(SymbolsRounded.Add, null) },
        )
      }
    }
  }
}

@Composable
private fun UserPresetNameDialog(
  name: String,
  title: String,
  onConfirm: (String) -> Unit,
  onDismissRequest: () -> Unit,
) {
  var textFieldValue by remember(name) {
    mutableStateOf(TextFieldValue(name, selection = TextRange(name.length)))
  }

  fun confirm() {
    val text = textFieldValue.text
    if (text.isBlank()) return
    onConfirm(text)
  }

  SimpleAlertDialogConfirmation(
    title = title,
    text = {
      val focusRequester = remember { FocusRequester() }
      LaunchedEffect(Unit) { focusRequester.requestFocus() }

      OutlinedTextField(
        modifier = Modifier
          .focusRequester(focusRequester)
          .onPreviewKeyEvent { keyEvent ->
            if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyDown) {
              confirm()
              true
            } else {
              false
            }
          },
        value = textFieldValue,
        onValueChange = { textFieldValue = it },
        label = { Text(stringResource(Res.string.name)) },
        singleLine = true,
      )
    },
    onConfirm = ::confirm,
    onDismissRequest = onDismissRequest,
    confirmEnabled = textFieldValue.text.isNotBlank(),
  )
}

@Composable
private fun UserPresetDeleteDialog(
  userPreset: UserPreset,
  onConfirm: () -> Unit,
  onDismissRequest: () -> Unit,
) {
  SimpleAlertDialogConfirmation(
    title = userPreset.name,
    text = { Text(stringResource(Res.string.user_presets_delete_confirmation)) },
    onConfirm = onConfirm,
    onDismissRequest = onDismissRequest,
    confirmLabel = stringResource(Res.string.delete),
  )
}

private val PresetViewReadyCornerRadius = 24.dp
