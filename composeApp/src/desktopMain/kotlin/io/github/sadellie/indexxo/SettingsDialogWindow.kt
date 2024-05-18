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

package io.github.sadellie.indexxo

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberDialogState
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.Navigator
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.settings
import indexxo.composeapp.generated.resources.settings_dialog_app_version
import indexxo.composeapp.generated.resources.settings_dialog_app_version_support
import indexxo.composeapp.generated.resources.settings_dialog_data_folder
import indexxo.composeapp.generated.resources.settings_dialog_data_folder_support
import indexxo.composeapp.generated.resources.settings_dialog_privacy_policy
import indexxo.composeapp.generated.resources.settings_dialog_source_code
import indexxo.composeapp.generated.resources.settings_dialog_theming_mode
import indexxo.composeapp.generated.resources.settings_dialog_theming_mode_auto
import indexxo.composeapp.generated.resources.settings_dialog_theming_mode_dark
import indexxo.composeapp.generated.resources.settings_dialog_theming_mode_light
import indexxo.composeapp.generated.resources.settings_dialog_theming_mode_support
import indexxo.composeapp.generated.resources.settings_dialog_third_party_licenses
import io.github.sadellie.indexxo.core.common.openDataFolder
import io.github.sadellie.indexxo.core.common.openLicenses
import io.github.sadellie.indexxo.core.common.openLink
import io.github.sadellie.indexxo.core.common.stateIn
import io.github.sadellie.indexxo.core.data.AppConfig
import io.github.sadellie.indexxo.core.data.PreferencesRepository
import io.github.sadellie.indexxo.core.designsystem.component.EmptyScreen
import io.github.sadellie.indexxo.core.designsystem.component.ListItem
import io.github.sadellie.indexxo.core.designsystem.component.RowWithScrollbar
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Code
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Copyright
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.DarkMode
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Folder
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.HdrAuto
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Info
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.LightMode
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Palette
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Policy
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded
import io.github.sadellie.indexxo.core.designsystem.theme.Previewer
import io.github.sadellie.indexxo.core.model.IndexxoPreferences
import io.github.sadellie.indexxo.core.model.ThemingMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsDialogWindow(
  onCloseRequest: () -> Unit
) {
  val dialogWindowState = rememberDialogState(
    width = 400.dp, // Compact
    height = 500.dp, // Medium
  )
  IndexxoDialogWindow(
    onCloseRequest = onCloseRequest,
    state = dialogWindowState,
    title = stringResource(Res.string.settings),
    onPreviewKeyEvent = {
      if (it.key == Key.Escape && it.type == KeyEventType.KeyDown) {
        onCloseRequest()
        true
      } else {
        false
      }
    },
  ) {
    Navigator(DialogSettingsScreen)
  }
}

private object DialogSettingsScreen : Screen {
  private fun readResolve(): Any = DialogSettingsScreen

  @Composable
  override fun Content() {
    val dialogSettingsScreenModel = koinScreenModel<DialogSettingsScreenModel>()
    when (val preferences = dialogSettingsScreenModel.preferences.collectAsState().value) {
      null -> EmptyScreen()
      else -> DialogSettingsScreenView(preferences, dialogSettingsScreenModel::updateThemingMode)
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogSettingsScreenView(
  preferences: IndexxoPreferences,
  updateThemingMode: (ThemingMode) -> Unit,
) {
  Scaffold { paddingValues ->
    val scrollState = rememberScrollState()
    RowWithScrollbar(
      modifier = Modifier.padding(paddingValues),
      scrollState = scrollState,
    ) {
      Column(
        modifier = Modifier.verticalScroll(scrollState),
      ) {
        ListItem(
          leadingContent = { Icon(SymbolsRounded.Palette, null) },
          headlineContent = { Text(stringResource(Res.string.settings_dialog_theming_mode)) },
          supportingContent = { Text(stringResource(Res.string.settings_dialog_theming_mode_support)) },
        )
        Row(
          modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .wrapContentWidth(),
        ) {
          SingleChoiceSegmentedButtonRow(modifier = Modifier.padding(56.dp, 8.dp, 24.dp, 2.dp)) {
            SegmentedButton(
              label = { Text(stringResource(Res.string.settings_dialog_theming_mode_auto)) },
              onClick = { updateThemingMode(ThemingMode.AUTO) },
              selected = preferences.themingMode == ThemingMode.AUTO,
              icon = { Icon(SymbolsRounded.HdrAuto, null) },
              shape = SegmentedButtonDefaults.itemShape(0, 3),
            )
            SegmentedButton(
              label = { Text(stringResource(Res.string.settings_dialog_theming_mode_light)) },
              onClick = { updateThemingMode(ThemingMode.FORCE_LIGHT) },
              selected = preferences.themingMode == ThemingMode.FORCE_LIGHT,
              icon = { Icon(SymbolsRounded.LightMode, null) },
              shape = SegmentedButtonDefaults.itemShape(1, 3),
            )
            SegmentedButton(
              label = { Text(stringResource(Res.string.settings_dialog_theming_mode_dark)) },
              onClick = { updateThemingMode(ThemingMode.FORCE_DARK) },
              selected = preferences.themingMode == ThemingMode.FORCE_DARK,
              icon = { Icon(SymbolsRounded.DarkMode, null) },
              shape = SegmentedButtonDefaults.itemShape(2, 3),
            )
          }
        }
        ListItem(
          headlineText = stringResource(Res.string.settings_dialog_third_party_licenses),
          icon = SymbolsRounded.Copyright,
          modifier = Modifier.clickable { openLicenses() },
        )
        ListItem(
          headlineText = stringResource(Res.string.settings_dialog_privacy_policy),
          icon = SymbolsRounded.Policy,
          modifier = Modifier.clickable { openLink("https://sadellie.github.io/indexxo/privacy") },
        )
        ListItem(
          headlineText = stringResource(Res.string.settings_dialog_source_code),
          icon = SymbolsRounded.Code,
          modifier = Modifier.clickable { openLink("https://github.com/sadellie/indexxo") },
        )
        ListItem(
          headlineText = stringResource(Res.string.settings_dialog_data_folder),
          supportingText = stringResource(Res.string.settings_dialog_data_folder_support),
          icon = SymbolsRounded.Folder,
          modifier = Modifier.clickable { openDataFolder() },
        )
        ListItem(
          headlineText = stringResource(Res.string.settings_dialog_app_version),
          supportingText = stringResource(
            Res.string.settings_dialog_app_version_support,
            AppConfig.appVersion,
            AppConfig.appVersionName,
          ),
          icon = SymbolsRounded.Info,
        )
      }
    }
  }
}

internal class DialogSettingsScreenModel(
  val preferencesRepository: PreferencesRepository
) : ScreenModel {
  val preferences = preferencesRepository.indexxoPreferencesFlow
    .stateIn(screenModelScope, null)

  fun updateThemingMode(themingMode: ThemingMode) = screenModelScope.launch(Dispatchers.IO) {
    preferencesRepository.updateThemingMode(themingMode)
  }
}

@Composable
@Preview
private fun PreviewDialogSettingsScreenView() = Previewer {
  DialogSettingsScreenView(
    preferences = IndexxoPreferences(
      presetId = 0,
      themingMode = ThemingMode.FORCE_DARK,
    ),
    updateThemingMode = {},
  )
}
