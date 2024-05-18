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

package io.github.sadellie.indexxo.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.duplicate_file_names
import indexxo.composeapp.generated.resources.duplicate_folder_names
import indexxo.composeapp.generated.resources.duplicate_hashes
import indexxo.composeapp.generated.resources.empty_files
import indexxo.composeapp.generated.resources.empty_folders
import indexxo.composeapp.generated.resources.settings_analyzers
import indexxo.composeapp.generated.resources.similar_images
import indexxo.composeapp.generated.resources.similar_videos
import io.github.sadellie.indexxo.core.database.model.UserPreset
import io.github.sadellie.indexxo.core.designsystem.component.EmptyScreen
import io.github.sadellie.indexxo.core.designsystem.component.ListItem
import io.github.sadellie.indexxo.core.designsystem.component.NavigateUpButton
import io.github.sadellie.indexxo.core.designsystem.component.RowWithScrollbar
import io.github.sadellie.indexxo.core.designsystem.component.ScaffoldWithTopBar
import io.github.sadellie.indexxo.feature.common.getSettingDetailScreenModel
import io.github.sadellie.indexxo.feature.similarimagessetting.SimilarImagesSettingScreen
import io.github.sadellie.indexxo.feature.similarvideossettings.SimilarVideosSettingsScreen
import org.jetbrains.compose.resources.stringResource

data class AnalyzersScreen(val presetId: Int) : Screen {
  @Composable
  override fun Content() {
    val localNavigator = LocalNavigator.currentOrThrow
    val screenModel = getSettingDetailScreenModel(presetId)
    when (val userPresetValue = screenModel.userPreset.collectAsState().value) {
      null -> EmptyScreen()
      else -> AnalyzersScreenView(
        userPreset = userPresetValue,
        updateUserPreset = screenModel::updateUserPreset,
        navigateUp = localNavigator::pop,
        onSimilarImagesSettingsClick = { localNavigator.push(SimilarImagesSettingScreen(it)) },
        onSimilarVideosSettingsClick = { localNavigator.push(SimilarVideosSettingsScreen(it)) },
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyzersScreenView(
  userPreset: UserPreset,
  updateUserPreset: (UserPreset) -> Unit,
  navigateUp: () -> Unit,
  onSimilarImagesSettingsClick: (id: Int) -> Unit,
  onSimilarVideosSettingsClick: (id: Int) -> Unit,
) {
  ScaffoldWithTopBar(
    title = stringResource(Res.string.settings_analyzers),
    navigationIcon = { NavigateUpButton(navigateUp) },
  ) { paddingValues ->
    val scrollState = rememberScrollState()

    RowWithScrollbar(modifier = Modifier.padding(paddingValues), scrollState = scrollState) {
      Column(modifier = Modifier.weight(1f).verticalScroll(scrollState)) {
        ListItem(
          headlineText = stringResource(Res.string.duplicate_hashes),
          checked = userPreset.isDuplicateHashesEnabled,
          onCheckedChange = { updateUserPreset(userPreset.copy(isDuplicateHashesEnabled = it)) },
        )

        ListItem(
          headlineText = stringResource(Res.string.duplicate_file_names),
          checked = userPreset.isDuplicateFileNamesEnabled,
          onCheckedChange = { updateUserPreset(userPreset.copy(isDuplicateFileNamesEnabled = it)) },
        )

        ListItem(
          headlineText = stringResource(Res.string.duplicate_folder_names),
          checked = userPreset.isDuplicateFolderNamesEnabled,
          onCheckedChange = { updateUserPreset(userPreset.copy(isDuplicateFolderNamesEnabled = it)) },
        )

        ListItem(
          headlineText = stringResource(Res.string.empty_folders),
          checked = userPreset.isEmptyFoldersEnabled,
          onCheckedChange = { updateUserPreset(userPreset.copy(isEmptyFoldersEnabled = it)) },
        )

        ListItem(
          headlineText = stringResource(Res.string.empty_files),
          checked = userPreset.isEmptyFilesEnabled,
          onCheckedChange = { updateUserPreset(userPreset.copy(isEmptyFilesEnabled = it)) },
        )

        ListItem(
          headlineText = stringResource(Res.string.similar_images),
          checked = userPreset.isSimilarImagesEnabled,
          onCheckedChange = { updateUserPreset(userPreset.copy(isSimilarImagesEnabled = it)) },
          onAdditionalContentClick = { onSimilarImagesSettingsClick(userPreset.id) },
        )

        ListItem(
          headlineText = stringResource(Res.string.similar_videos),
          checked = userPreset.isSimilarVideosEnabled,
          onCheckedChange = { updateUserPreset(userPreset.copy(isSimilarVideosEnabled = it)) },
          onAdditionalContentClick = { onSimilarVideosSettingsClick(userPreset.id) },
        )
      }
    }
  }
}
