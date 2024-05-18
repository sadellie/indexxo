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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.actions_tab_duplicate_file_names_body
import indexxo.composeapp.generated.resources.actions_tab_duplicate_file_names_title
import indexxo.composeapp.generated.resources.actions_tab_duplicate_folder_names_body
import indexxo.composeapp.generated.resources.actions_tab_duplicate_folder_names_title
import indexxo.composeapp.generated.resources.actions_tab_duplicate_hashes_body
import indexxo.composeapp.generated.resources.actions_tab_duplicate_hashes_title
import indexxo.composeapp.generated.resources.actions_tab_empty_actions
import indexxo.composeapp.generated.resources.actions_tab_empty_files_body
import indexxo.composeapp.generated.resources.actions_tab_empty_files_title
import indexxo.composeapp.generated.resources.actions_tab_empty_folders_body
import indexxo.composeapp.generated.resources.actions_tab_empty_folders_title
import indexxo.composeapp.generated.resources.actions_tab_resolve
import indexxo.composeapp.generated.resources.actions_tab_similar_images_body
import indexxo.composeapp.generated.resources.actions_tab_similar_images_title
import indexxo.composeapp.generated.resources.actions_tab_similar_videos_body
import indexxo.composeapp.generated.resources.actions_tab_similar_videos_title
import indexxo.composeapp.generated.resources.actions_tab_title
import indexxo.composeapp.generated.resources.rescan
import io.github.sadellie.indexxo.core.designsystem.component.EmptyScreen
import io.github.sadellie.indexxo.core.designsystem.component.RowWithScrollbar
import io.github.sadellie.indexxo.core.designsystem.component.ScaffoldWithTopBar
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Checklist
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

data object ActionsTab : Tab {

  @Composable
  override fun Content() = ActionsTabContent()

  override val options: TabOptions
    @Composable
    get() {
      val title = stringResource(Res.string.actions_tab_title)
      val icon = rememberVectorPainter(SymbolsRounded.Checklist)

      return remember(title) { TabOptions(index = 0u, title = title, icon = icon) }
    }
}

@Composable
expect fun Tab.ActionsTabContent()

sealed class ActionsTabUIState {
  data object Loading : ActionsTabUIState()

  data object Empty : ActionsTabUIState()

  data class Ready(
    val duplicateHashes: Int,
    val duplicateFileNames: Int,
    val duplicateFolderNames: Int,
    val emptyFiles: Int,
    val emptyFolders: Int,
    val similarImages: Int,
    val similarVideos: Int,
  ) : ActionsTabUIState()
}

enum class ProblemTypeAction(
  val title: StringResource,
  val body: StringResource
) {
  DUPLICATE_HASHES(
    Res.string.actions_tab_duplicate_hashes_title,
    Res.string.actions_tab_duplicate_hashes_body,
  ),
  DUPLICATE_FILE_NAMES(
    Res.string.actions_tab_duplicate_file_names_title,
    Res.string.actions_tab_duplicate_file_names_body,
  ),
  DUPLICATE_FOLDER_NAMES(
    Res.string.actions_tab_duplicate_folder_names_title,
    Res.string.actions_tab_duplicate_folder_names_body,
  ),
  EMPTY_FILES(
    Res.string.actions_tab_empty_files_title,
    Res.string.actions_tab_empty_files_body,
  ),
  EMPTY_FOLDERS(
    Res.string.actions_tab_empty_folders_title,
    Res.string.actions_tab_empty_folders_body,
  ),
  SIMILAR_IMAGES(
    Res.string.actions_tab_similar_images_title,
    Res.string.actions_tab_similar_images_body,
  ),
  SIMILAR_VIDEOS(
    Res.string.actions_tab_similar_videos_title,
    Res.string.actions_tab_similar_videos_body,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionsTabView(
  title: String,
  uiState: ActionsTabUIState,
  navigateToProblemsScreen: (ProblemTypeAction) -> Unit,
  navigateToSettings: () -> Unit,
) {
  ScaffoldWithTopBar(title = title) { paddingValues ->
    when (uiState) {
      ActionsTabUIState.Loading -> EmptyScreen()
      ActionsTabUIState.Empty -> EmptyActions(
        modifier = Modifier.padding(paddingValues).fillMaxSize(),
        navigateToSettings = navigateToSettings,
      )

      is ActionsTabUIState.Ready -> ActionsColumn(
        modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp).fillMaxWidth(),
        uiState = uiState,
        navigateToProblemsScreen = navigateToProblemsScreen,
      )
    }
  }
}

@Composable
private fun EmptyActions(
  modifier: Modifier,
  navigateToSettings: () -> Unit,
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Text(
      text = stringResource(Res.string.actions_tab_empty_actions),
      style = MaterialTheme.typography.bodyLarge,
      textAlign = TextAlign.Center
    )
    TextButton(navigateToSettings) {
      Text(stringResource(Res.string.rescan))
    }
  }
}

@Composable
private fun ActionsColumn(
  modifier: Modifier = Modifier,
  uiState: ActionsTabUIState.Ready,
  navigateToProblemsScreen: (ProblemTypeAction) -> Unit,
) {
  val listState = rememberLazyListState()

  RowWithScrollbar(listState = listState, modifier = modifier) {
    LazyColumn(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(vertical = 8.dp),
      state = listState,
    ) {
      actionCard(
        count = uiState.duplicateHashes,
        problemTypeAction = ProblemTypeAction.DUPLICATE_HASHES,
        onClick = { navigateToProblemsScreen(ProblemTypeAction.DUPLICATE_HASHES) },
      )

      actionCard(
        count = uiState.duplicateFileNames,
        problemTypeAction = ProblemTypeAction.DUPLICATE_FILE_NAMES,
        onClick = { navigateToProblemsScreen(ProblemTypeAction.DUPLICATE_FILE_NAMES) },
      )

      actionCard(
        count = uiState.duplicateFolderNames,
        problemTypeAction = ProblemTypeAction.DUPLICATE_FOLDER_NAMES,
        onClick = { navigateToProblemsScreen(ProblemTypeAction.DUPLICATE_FOLDER_NAMES) },
      )

      actionCard(
        count = uiState.emptyFiles,
        problemTypeAction = ProblemTypeAction.EMPTY_FILES,
        onClick = { navigateToProblemsScreen(ProblemTypeAction.EMPTY_FILES) },
      )

      actionCard(
        count = uiState.emptyFolders,
        problemTypeAction = ProblemTypeAction.EMPTY_FOLDERS,
        onClick = { navigateToProblemsScreen(ProblemTypeAction.EMPTY_FOLDERS) },
      )

      actionCard(
        count = uiState.similarImages,
        problemTypeAction = ProblemTypeAction.SIMILAR_IMAGES,
        onClick = { navigateToProblemsScreen(ProblemTypeAction.SIMILAR_IMAGES) },
      )

      actionCard(
        count = uiState.similarVideos,
        problemTypeAction = ProblemTypeAction.SIMILAR_VIDEOS,
        onClick = { navigateToProblemsScreen(ProblemTypeAction.SIMILAR_VIDEOS) },
      )
    }
  }
}

private fun LazyListScope.actionCard(
  modifier: Modifier = Modifier,
  count: Int,
  problemTypeAction: ProblemTypeAction,
  onClick: () -> Unit,
) {
  if (count == 0) return

  item {
    OutlinedCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
      Text(
        text = stringResource(problemTypeAction.title),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp),
      )
      Text(
        text = stringResource(problemTypeAction.body, count),
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        maxLines = 2,
        minLines = 2,
      )
      TextButton(
        onClick = onClick,
        modifier = Modifier.align(Alignment.End)
          .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
      ) {
        Text(stringResource(Res.string.actions_tab_resolve))
      }
    }
  }
}
