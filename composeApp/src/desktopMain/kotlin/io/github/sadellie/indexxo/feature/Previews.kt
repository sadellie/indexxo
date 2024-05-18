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

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.sadellie.indexxo.core.common.localDateTimeNow
import io.github.sadellie.indexxo.core.database.model.UserPreset
import io.github.sadellie.indexxo.core.designsystem.theme.Previewer
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObjectImpl
import io.github.sadellie.indexxo.core.ui.FileGridItem
import io.github.sadellie.indexxo.core.ui.FileListItem
import io.github.sadellie.indexxo.feature.actions.ActionsTab
import io.github.sadellie.indexxo.feature.home.CompactScreen
import io.github.sadellie.indexxo.feature.home.ExpandedScreen
import io.github.sadellie.indexxo.feature.home.HomeScreen
import io.github.sadellie.indexxo.feature.home.MediumScreen
import io.github.sadellie.indexxo.feature.similarimagessetting.SimilarImagesSettingScreenViewReady
import io.github.sadellie.indexxo.feature.similarvideossettings.SimilarVideosSettingsScreenViewReady
import okio.Path.Companion.toPath

@Composable
@Preview
private fun PreviewExpandedScreen() = Previewer {
  ExpandedScreen(
    title = "Preset 123",
    tabs = HomeScreen.topTabs,
    containerColor = MaterialTheme.colorScheme.surfaceContainer,
    onSearchClick = {},
    onRescanClick = {},
    currentTab = ActionsTab,
    onTabClick = {},
    content = { Text("Some content") },
  )
}

@Composable
@Preview
private fun PreviewMediumScreen() = Previewer {
  MediumScreen(
    title = "Preset 123",
    tabs = HomeScreen.topTabs,
    containerColor = MaterialTheme.colorScheme.surfaceContainer,
    onSearchClick = {},
    onRescanClick = {},
    currentTab = ActionsTab,
    onTabClick = {},
    content = { Text("Some content") },
  )
}

@Composable
@Preview
private fun PreviewCompactScreen() = Previewer {
  CompactScreen(
    title = "Preset 123",
    tabs = HomeScreen.topTabs,
    containerColor = MaterialTheme.colorScheme.surfaceContainer,
    onSearchClick = {},
    onRescanClick = {},
    currentTab = ActionsTab,
    onTabClick = {},
    content = { Text("Some content") },
  )
}

@Composable
@Preview
private fun PreviewSimilarImageSettingScreenViewReady() = Previewer {
  SimilarImagesSettingScreenViewReady(
    userPreset = UserPreset(
      id = 0,
      name = "Test",
      maxThreads = 3,
      includedDirectories = emptyList(),
      excludedDirectories = emptyList(),
      includedFiles = emptyList(),
      excludedFiles = emptyList(),
      includedExtensions = emptyList(),
      excludedExtensions = emptyList(),
      isDuplicateHashesEnabled = true,
      isDuplicateFileNamesEnabled = true,
      isDuplicateFolderNamesEnabled = true,
      isEmptyFoldersEnabled = true,
      isEmptyFilesEnabled = false,
      isSimilarImagesEnabled = true,
      similarImagesMinSimilarity = 0.8f,
      isSimilarImagesImproveAccuracy = true,
      isSimilarVideosEnabled = true,
      similarVideosMinimalHashSimilarity = 0.5f,
      similarVideosMinimalFrameSimilarity = 0.8f,
      similarVideosFPS = 60,
    ),
    navigateUp = {},
    updateUserPreset = {},
  )
}

@Composable
@Preview
private fun PreviewSimilarVideosSettingsScreenViewReady() = Previewer {
  SimilarVideosSettingsScreenViewReady(
    userPreset = UserPreset(
      id = 0,
      name = "Test",
      maxThreads = 3,
      includedDirectories = emptyList(),
      excludedDirectories = emptyList(),
      includedFiles = emptyList(),
      excludedFiles = emptyList(),
      includedExtensions = emptyList(),
      excludedExtensions = emptyList(),
      isDuplicateHashesEnabled = true,
      isDuplicateFileNamesEnabled = true,
      isDuplicateFolderNamesEnabled = true,
      isEmptyFoldersEnabled = true,
      isEmptyFilesEnabled = false,
      isSimilarImagesEnabled = true,
      similarImagesMinSimilarity = 0.8f,
      isSimilarImagesImproveAccuracy = true,
      isSimilarVideosEnabled = true,
      similarVideosMinimalHashSimilarity = 0.5f,
      similarVideosMinimalFrameSimilarity = 0.8f,
      similarVideosFPS = 60,
    ),
    navigateUp = {},
    updateUserPreset = {},
  )
}

@Composable
@Preview
private fun PreviewAnalyzersScreenView() = Previewer {
  AnalyzersScreenView(
    userPreset = UserPreset(
      id = 0,
      name = "Test",
      maxThreads = 3,
      includedDirectories = emptyList(),
      excludedDirectories = emptyList(),
      includedFiles = emptyList(),
      excludedFiles = emptyList(),
      includedExtensions = emptyList(),
      excludedExtensions = emptyList(),
      isDuplicateHashesEnabled = true,
      isDuplicateFileNamesEnabled = true,
      isDuplicateFolderNamesEnabled = true,
      isEmptyFoldersEnabled = true,
      isEmptyFilesEnabled = false,
      isSimilarImagesEnabled = true,
      similarImagesMinSimilarity = 0.8f,
      isSimilarImagesImproveAccuracy = true,
      isSimilarVideosEnabled = true,
      similarVideosMinimalHashSimilarity = 0.5f,
      similarVideosMinimalFrameSimilarity = 0.8f,
      similarVideosFPS = 60,
    ),
    updateUserPreset = {},
    navigateUp = {},
    onSimilarImagesSettingsClick = {},
    onSimilarVideosSettingsClick = {},
  )
}

@Preview
@Composable
private fun PreviewFileItem() = Previewer {
  Column {
    FileListItem(
      modifier = Modifier,
      item =
      IndexedObjectImpl(
        path = "/path/to/file.txt".toPath(),
        parentPath = "".toPath(),
        sizeBytes = 2048,
        fileCategory = FileCategory.OTHER,
        createdDate = localDateTimeNow(),
        modifiedDate = localDateTimeNow(),
      ),
      isSelected = false,
      onClick = {},
      onOpen = {},
      onShowInExplorer = {},
    )

    FileListItem(
      modifier = Modifier,
      item =
      IndexedObjectImpl(
        path = "/path/to/file.txt".toPath(),
        parentPath = "".toPath(),
        sizeBytes = 2048,
        fileCategory = FileCategory.OTHER,
        createdDate = localDateTimeNow(),
        modifiedDate = localDateTimeNow(),
      ),
      isSelected = true,
      onClick = {},
      onOpen = {},
      onShowInExplorer = {},
    )

    FileGridItem(
      modifier = Modifier,
      item =
      IndexedObjectImpl(
        path = "/path/to/photo1.ong".toPath(),
        parentPath = "".toPath(),
        sizeBytes = 2048,
        fileCategory = FileCategory.OTHER,
        createdDate = localDateTimeNow(),
        modifiedDate = localDateTimeNow(),
      ),
      isSelected = false,
      onClick = {},
      onOpen = {},
      onShowInExplorer = {},
    )

    FileGridItem(
      modifier = Modifier,
      item =
      IndexedObjectImpl(
        path = "/path/to/photo1.ong".toPath(),
        parentPath = "".toPath(),
        sizeBytes = 2048,
        fileCategory = FileCategory.OTHER,
        createdDate = localDateTimeNow(),
        modifiedDate = localDateTimeNow(),
      ),
      isSelected = true,
      onClick = {},
      onOpen = {},
      onShowInExplorer = {},
    )
  }
}
