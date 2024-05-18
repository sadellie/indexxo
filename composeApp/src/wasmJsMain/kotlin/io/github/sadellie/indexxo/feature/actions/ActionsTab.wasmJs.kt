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

package io.github.sadellie.indexxo.feature.actions

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.dismiss
import io.github.sadellie.indexxo.core.common.navigateToDownloadPage
import io.github.sadellie.indexxo.core.designsystem.component.SimpleAlertDialogConfirmation
import io.github.sadellie.indexxo.feature.settings.SettingsScreen
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun Tab.ActionsTabContent() {
  val localNavigator = LocalNavigator.currentOrThrow
  var showDemoDialog by remember { mutableStateOf(false) }

  val uiState = ActionsTabUIState.Ready(
    duplicateHashes = 234,
    duplicateFileNames = 423,
    duplicateFolderNames = 76,
    emptyFolders = 78,
    emptyFiles = 63,
    similarImages = 35,
    similarVideos = 26,
  )

  ActionsTabView(
    title = ActionsTab.options.title,
    uiState = uiState,
    navigateToProblemsScreen = { showDemoDialog = true },
    navigateToSettings = { localNavigator.parent?.popUntil { it == SettingsScreen } },
  )

  if (showDemoDialog) {
    SimpleAlertDialogConfirmation(
      title = "Demo app is cool but…",
      text = {
        Text("What about your storage? Download Indexxo now and see what issues your computer has.")
      },
      onConfirm = ::navigateToDownloadPage,
      onDismissRequest = { showDemoDialog = false },
      confirmLabel = "Download",
      dismissLabel = stringResource(Res.string.dismiss),
    )
  }
}
