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

package io.github.sadellie.indexxo.feature.similarimagessetting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.dismiss
import indexxo.composeapp.generated.resources.similar_images
import indexxo.composeapp.generated.resources.similar_images_settings_match_colors
import indexxo.composeapp.generated.resources.similar_images_settings_match_colors_support
import indexxo.composeapp.generated.resources.similar_images_settings_minimal_similarity
import indexxo.composeapp.generated.resources.similar_images_settings_minimal_similarity_support
import indexxo.composeapp.generated.resources.similar_images_settings_minimal_similarity_too_low
import indexxo.composeapp.generated.resources.similar_images_settings_minimal_similarity_too_low_support
import io.github.sadellie.indexxo.core.database.model.UserPreset
import io.github.sadellie.indexxo.core.designsystem.component.AnnoyingBox
import io.github.sadellie.indexxo.core.designsystem.component.EmptyScreen
import io.github.sadellie.indexxo.core.designsystem.component.ListItem
import io.github.sadellie.indexxo.core.designsystem.component.ListItemWithSlider
import io.github.sadellie.indexxo.core.designsystem.component.NavigateUpButton
import io.github.sadellie.indexxo.core.designsystem.component.ScaffoldWithTopBar
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Architecture
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Speed
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Warning
import io.github.sadellie.indexxo.feature.common.getSettingDetailScreenModel
import org.jetbrains.compose.resources.stringResource

data class SimilarImagesSettingScreen(private val presetId: Int) : Screen {
  @Composable
  override fun Content() {
    val localNavigator = LocalNavigator.currentOrThrow
    val screenModel = getSettingDetailScreenModel(presetId)
    when (val userPreset = screenModel.userPreset.collectAsState().value) {
      null -> EmptyScreen()
      else -> SimilarImagesSettingScreenViewReady(
        userPreset = userPreset,
        navigateUp = localNavigator::pop,
        updateUserPreset = screenModel::updateUserPreset,
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimilarImagesSettingScreenViewReady(
  userPreset: UserPreset,
  navigateUp: () -> Unit,
  updateUserPreset: (UserPreset) -> Unit,
) {
  ScaffoldWithTopBar(
    navigationIcon = { NavigateUpButton(navigateUp) },
    title = stringResource(Res.string.similar_images),
  ) { paddingValues ->
    Column(modifier = Modifier.padding(paddingValues)) {


      ListItem(
        headlineText = stringResource(Res.string.similar_images_settings_match_colors),
        supportingText = stringResource(Res.string.similar_images_settings_match_colors_support),
        icon = SymbolsRounded.Speed,
        switchState = userPreset.isSimilarImagesImproveAccuracy,
        onSwitchChange = { updateUserPreset(userPreset.copy(isSimilarImagesImproveAccuracy = it)) },
      )

      MinSimilaritySettingItem(
        updateUserPreset = updateUserPreset,
        userPreset = userPreset,
      )
    }
  }
}

@Composable
private fun ColumnScope.MinSimilaritySettingItem(
  updateUserPreset: (UserPreset) -> Unit,
  userPreset: UserPreset
) {
  ListItemWithSlider(
    headlineText = stringResource(Res.string.similar_images_settings_minimal_similarity),
    supportingContent = {
      Text(stringResource(Res.string.similar_images_settings_minimal_similarity_support))
    },
    leadingContent = { Icon(SymbolsRounded.Architecture, null) },
    initialValue = userPreset.similarImagesMinSimilarity,
    onValueChange = {
      updateUserPreset(userPreset.copy(similarImagesMinSimilarity = it))
    },
    valueRange = 0.1f..1f,
  )

  var dismissedFastModeMessage by rememberSaveable(userPreset.isSimilarImagesImproveAccuracy) {
    mutableStateOf(false)
  }

  val lowAccuracy = when {
      userPreset.isSimilarImagesImproveAccuracy ->
        userPreset.similarImagesMinSimilarity < LOW_ACCURACY_THRESHOLD_WITH_COLOR_MATCH

      else -> userPreset.similarImagesMinSimilarity < LOW_ACCURACY_THRESHOLD_NO_COLOR_MATCH
    }

  AnimatedVisibility(visible = !dismissedFastModeMessage and lowAccuracy) {
    AnnoyingBox(
      modifier = Modifier.padding(horizontal = 16.dp),
      imageVector = SymbolsRounded.Warning,
      title = stringResource(Res.string.similar_images_settings_minimal_similarity_too_low),
      support = stringResource(Res.string.similar_images_settings_minimal_similarity_too_low_support),
      actionTitle = stringResource(Res.string.dismiss),
      onActionClick = { dismissedFastModeMessage = true },
    )
  }
}

private const val LOW_ACCURACY_THRESHOLD_WITH_COLOR_MATCH = 0.75f
private const val LOW_ACCURACY_THRESHOLD_NO_COLOR_MATCH = 0.9f
