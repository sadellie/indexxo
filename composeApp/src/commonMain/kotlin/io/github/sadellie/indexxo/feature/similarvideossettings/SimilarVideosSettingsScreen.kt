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

package io.github.sadellie.indexxo.feature.similarvideossettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.similar_videos
import indexxo.composeapp.generated.resources.similar_videos_settings_minimal_frame_rate
import indexxo.composeapp.generated.resources.similar_videos_settings_minimal_frame_rate_support
import indexxo.composeapp.generated.resources.similar_videos_settings_minimal_frames_similarity
import indexxo.composeapp.generated.resources.similar_videos_settings_minimal_frames_similarity_support
import indexxo.composeapp.generated.resources.similar_videos_settings_minimal_hash_similarity
import indexxo.composeapp.generated.resources.similar_videos_settings_minimal_hash_similarity_support
import io.github.sadellie.indexxo.core.database.model.UserPreset
import io.github.sadellie.indexxo.core.designsystem.component.EmptyScreen
import io.github.sadellie.indexxo.core.designsystem.component.ListItemWithSlider
import io.github.sadellie.indexxo.core.designsystem.component.NavigateUpButton
import io.github.sadellie.indexxo.core.designsystem.component.ScaffoldWithTopBar
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Architecture
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.AutoAwesomeMotion
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.FrameInspect
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded
import io.github.sadellie.indexxo.feature.common.getSettingDetailScreenModel
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

data class SimilarVideosSettingsScreen(private val presetId: Int) : Screen {
  @Composable
  override fun Content() {
    val localNavigator = LocalNavigator.currentOrThrow
    val screenModel = getSettingDetailScreenModel(presetId)
    when (val userPreset = screenModel.userPreset.collectAsState().value) {
      null -> EmptyScreen()
      else -> SimilarVideosSettingsScreenViewReady(
        userPreset = userPreset,
        navigateUp = localNavigator::pop,
        updateUserPreset = screenModel::updateUserPreset,
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimilarVideosSettingsScreenViewReady(
  userPreset: UserPreset,
  navigateUp: () -> Unit,
  updateUserPreset: (UserPreset) -> Unit,
) {
  ScaffoldWithTopBar(
    title = stringResource(Res.string.similar_videos),
    navigationIcon = { NavigateUpButton(navigateUp) },
  ) { paddingValues ->
    Column(modifier = Modifier.padding(paddingValues)) {
      ListItemWithSlider(
        headlineText = stringResource(Res.string.similar_videos_settings_minimal_hash_similarity),
        supportingContent = {
          Text(stringResource(Res.string.similar_videos_settings_minimal_hash_similarity_support))
        },
        leadingContent = { Icon(SymbolsRounded.Architecture, null) },
        initialValue = userPreset.similarVideosMinimalHashSimilarity,
        onValueChange = { updateUserPreset(userPreset.copy(similarVideosMinimalHashSimilarity = it)) },
        valueRange = 0.1f..1f,
      )

      ListItemWithSlider(
        headlineText = stringResource(Res.string.similar_videos_settings_minimal_frames_similarity),
        supportingContent = {
          Text(stringResource(Res.string.similar_videos_settings_minimal_frames_similarity_support))
        },
        leadingContent = { Icon(SymbolsRounded.FrameInspect, null) },
        initialValue = userPreset.similarVideosMinimalFrameSimilarity,
        onValueChange = { updateUserPreset(userPreset.copy(similarVideosMinimalFrameSimilarity = it)) },
        valueRange = 0.1f..1f,
      )

      ListItemWithSlider(
        headlineText = stringResource(Res.string.similar_videos_settings_minimal_frame_rate),
        supportingContent = {
          Text(stringResource(Res.string.similar_videos_settings_minimal_frame_rate_support))
        },
        leadingContent = { Icon(SymbolsRounded.AutoAwesomeMotion, null) },
        initialValue = userPreset.similarVideosFPS.toFloat(),
        onValueChange = { updateUserPreset(userPreset.copy(similarVideosFPS = it.roundToInt())) },
        valueRange = 1f..120f,
        steps = 119,
        formatValueLabel = { "${it.roundToInt()} FPS" },
      )
    }
  }
}
