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

package io.github.sadellie.indexxo.core.ui

import androidx.compose.animation.Crossfade
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.discard
import indexxo.composeapp.generated.resources.move_to_trash
import io.github.sadellie.indexxo.core.common.formatBytes
import io.github.sadellie.indexxo.core.common.localDateTimeNow
import io.github.sadellie.indexxo.core.common.openInSystem
import io.github.sadellie.indexxo.core.designsystem.component.ListHeader
import io.github.sadellie.indexxo.core.designsystem.component.NavigateUpButton
import io.github.sadellie.indexxo.core.designsystem.component.NoMoreItemsPlaceholder
import io.github.sadellie.indexxo.core.designsystem.component.RowWithScrollbar
import io.github.sadellie.indexxo.core.designsystem.component.ScaffoldWithTopBar
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.GridView
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.ViewList
import io.github.sadellie.indexxo.core.designsystem.theme.Previewer
import io.github.sadellie.indexxo.core.model.DuplicateHash
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObjectImpl
import io.github.sadellie.indexxo.core.model.IndexedObjectsGroup
import io.github.sadellie.indexxo.core.model.MoveToTrashProgress
import io.github.sadellie.indexxo.feature.common.IndexedObjectsFlatSelectorUIState
import io.github.sadellie.indexxo.feature.common.IndexedObjectsGroupSelectorUIState
import okio.Path
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.stringResource

@Composable
fun IndexedObjectsFlatSelectorView(
  uiState: IndexedObjectsFlatSelectorUIState,
  title: String,
  navigateUp: () -> Unit,
  onGalleryViewStateChange: (Boolean) -> Unit,
  onSelectPath: (Path) -> Unit,
  onMoveToTrashStart: () -> Unit,
  onMoveToTrashCancel: () -> Unit,
  onDiscard: () -> Unit,
) {
  if (uiState.indexedObjects.isEmpty()) {
    IndexedObjectsSelectorViewEmptyPlaceholder(title, navigateUp)
    return
  }

  IndexedObjectsSelectorListView(
    title = title,
    navigateUp = navigateUp,
    isGalleryViewEnabled = uiState.isGalleryViewEnabled,
    onGalleryViewStateChange = { onGalleryViewStateChange(!uiState.isGalleryViewEnabled) },
    selectedPaths = uiState.selectedPaths,
    moveToTrashProgress = uiState.moveToTrashProgress,
    onMoveToTrashStart = onMoveToTrashStart,
    onMoveToTrashCancel = onMoveToTrashCancel,
    onDiscard = onDiscard,
    listContent = {
      items(uiState.indexedObjects) { indexedObject ->
        FileListItem(
          item = indexedObject,
          isSelected = indexedObject.path in uiState.selectedPaths,
          onClick = { onSelectPath(indexedObject.path) },
          onOpen = { indexedObject.path.openInSystem() },
          onShowInExplorer = { indexedObject.path.parent?.openInSystem() },
        )
      }
    },
    gridContent = {
      items(uiState.indexedObjects) { indexedObject ->
        FileGridItem(
          item = indexedObject,
          isSelected = indexedObject.path in uiState.selectedPaths,
          onClick = { onSelectPath(indexedObject.path) },
          onOpen = { indexedObject.path.openInSystem() },
          onShowInExplorer = { indexedObject.path.parent?.openInSystem() },
        )
      }
    },
  )
}

@Composable
fun <T : IndexedObjectsGroup> IndexedObjectsGroupSelectorView(
  uiState: IndexedObjectsGroupSelectorUIState<T>,
  title: String,
  navigateUp: () -> Unit,
  onGalleryViewStateChange: (Boolean) -> Unit,
  onSelectPath: (Path) -> Unit,
  onSelectPaths: (Set<Path>) -> Unit,
  onMoveToTrashStart: () -> Unit,
  onMoveToTrashCancel: () -> Unit,
  onDiscard: () -> Unit,
  listHeader: (T) -> String = { formatBytes(it.totalSizeBytes) },
) {
  if (uiState.indexedObjectsGroups.isEmpty()) {
    IndexedObjectsSelectorViewEmptyPlaceholder(title, navigateUp)
    return
  }

  IndexedObjectsSelectorListView(
    title = title,
    navigateUp = navigateUp,
    isGalleryViewEnabled = uiState.isGalleryViewEnabled,
    onGalleryViewStateChange = { onGalleryViewStateChange(!uiState.isGalleryViewEnabled) },
    selectedPaths = uiState.selectedPaths,
    moveToTrashProgress = uiState.moveToTrashProgress,
    onMoveToTrashStart = onMoveToTrashStart,
    onMoveToTrashCancel = onMoveToTrashCancel,
    onDiscard = onDiscard,
    listContent = {
      uiState.indexedObjectsGroups.forEach { indexedObjectsGroup ->
        item {
          FileListHeader(
            indexedObjectsGroup = indexedObjectsGroup,
            listHeader = listHeader,
            onSelectPaths = onSelectPaths,
            selectedPaths = uiState.selectedPaths,
          )
        }

        items(indexedObjectsGroup.duplicates) { indexedObject ->
          FileListItem(
            item = indexedObject,
            isSelected = indexedObject.path in uiState.selectedPaths,
            onClick = { onSelectPath(indexedObject.path) },
            onOpen = { indexedObject.path.openInSystem() },
            onShowInExplorer = { indexedObject.path.parent?.openInSystem() },
          )
        }
      }
    },
    gridContent = {
      uiState.indexedObjectsGroups.forEach { indexedObjectsGroup ->
        item(span = { GridItemSpan(maxLineSpan) }) {
          FileListHeader(
            indexedObjectsGroup = indexedObjectsGroup,
            listHeader = listHeader,
            onSelectPaths = onSelectPaths,
            selectedPaths = uiState.selectedPaths,
          )
        }
        items(indexedObjectsGroup.duplicates) { indexedObject ->
          FileGridItem(
            item = indexedObject,
            isSelected = indexedObject.path in uiState.selectedPaths,
            onClick = { onSelectPath(indexedObject.path) },
            onOpen = { indexedObject.path.openInSystem() },
            onShowInExplorer = { indexedObject.path.parent?.openInSystem() },
          )
        }
      }
    },
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndexedObjectsSelectorViewEmptyPlaceholder(
  title: String,
  navigateUp: () -> Unit,
) {
  ScaffoldWithTopBar(
    title = title,
    navigationIcon = { NavigateUpButton(navigateUp) },
  ) { paddingValues ->
    NoMoreItemsPlaceholder(Modifier.padding(paddingValues).fillMaxSize())
  }
}

@Composable
private fun IndexedObjectsSelectorListView(
  title: String,
  navigateUp: () -> Unit,
  isGalleryViewEnabled: Boolean,
  onGalleryViewStateChange: (Boolean) -> Unit,
  selectedPaths: Set<Path>,
  moveToTrashProgress: MoveToTrashProgress?,
  onMoveToTrashStart: () -> Unit,
  onMoveToTrashCancel: () -> Unit,
  onDiscard: () -> Unit,
  listContent: LazyListScope.() -> Unit,
  gridContent: LazyGridScope.() -> Unit,
) {
  IndexedObjectSelectorViewLayout(
    title = title,
    navigateUp = navigateUp,
    isGalleryViewEnabled = isGalleryViewEnabled,
    onGalleryViewStateChange = onGalleryViewStateChange,
    selectedPaths = selectedPaths,
    moveToTrashProgress = moveToTrashProgress,
    onMoveToTrashStart = onMoveToTrashStart,
    onMoveToTrashCancel = onMoveToTrashCancel,
    onDiscard = onDiscard,
  ) {
    Crossfade(
      modifier = Modifier.weight(1f),
      targetState = isGalleryViewEnabled,
    ) {
      if (it) {
        val gridState = rememberLazyGridState()
        RowWithScrollbar(Modifier.weight(1f), gridState) {
          LazyVerticalGrid(
            modifier = Modifier.weight(1f),
            columns = GridCells.Adaptive(176.dp),
            state = gridState,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = PaddingValues(8.dp),
            content = gridContent,
          )
        }
      } else {
        val listState = rememberLazyListState()
        RowWithScrollbar(Modifier.weight(1f), listState) {
          LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = PaddingValues(8.dp),
            state = listState,
            content = listContent,
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IndexedObjectSelectorViewLayout(
  title: String,
  navigateUp: () -> Unit,
  isGalleryViewEnabled: Boolean,
  onGalleryViewStateChange: (Boolean) -> Unit,
  selectedPaths: Set<Path>,
  moveToTrashProgress: MoveToTrashProgress?,
  onMoveToTrashStart: () -> Unit,
  onMoveToTrashCancel: () -> Unit,
  onDiscard: () -> Unit,
  content: @Composable ColumnScope.() -> Unit,
) {
  ScaffoldWithTopBar(
    navigationIcon = { NavigateUpButton(navigateUp) },
    actions = {
      IconButton(onClick = { onGalleryViewStateChange(isGalleryViewEnabled) }) {
        Crossfade(isGalleryViewEnabled) { galleryView ->
          if (galleryView) {
            Icon(SymbolsRounded.GridView, null)
          } else {
            Icon(SymbolsRounded.ViewList, null)
          }
        }
      }
    },
    title = title,
  ) { paddingValues ->
    Column(modifier = Modifier.padding(paddingValues)) {
      content()

      var showMoveToTrashConfirmationDialog by rememberSaveable {
        mutableStateOf<ConfirmationDialogState?>(null)
      }

      SelectorViewLayoutActionButtons(
        onDiscardClick = {
          showMoveToTrashConfirmationDialog = ConfirmationDialogState.DISCARD
        },
        onMoveToTrashClick = {
          showMoveToTrashConfirmationDialog = ConfirmationDialogState.MOVE_TO_TRASH
        },
        selectedPathsSize = selectedPaths.size,
      )

      when (showMoveToTrashConfirmationDialog) {
        ConfirmationDialogState.MOVE_TO_TRASH -> MoveToTrashConfirmationDialog(
          onConfirm = {
            onMoveToTrashStart()
            showMoveToTrashConfirmationDialog = null
          },
          onDismiss = { showMoveToTrashConfirmationDialog = null },
        )

        ConfirmationDialogState.DISCARD -> DiscardConfirmationDialog(
          onConfirm = {
            onDiscard()
            showMoveToTrashConfirmationDialog = null
          },
          onDismiss = { showMoveToTrashConfirmationDialog = null },
        )

        null -> Unit
      }

      when (moveToTrashProgress) {
        is MoveToTrashProgress.InProgress -> MoveToTrashDialogInProgress(
          moveToTrashProgress = moveToTrashProgress,
          onMoveToTrashCancel = onMoveToTrashCancel,
        )

        is MoveToTrashProgress.RefreshingIndex -> MoveToTrashDialogRefreshingIndex()

        null -> Unit
      }
    }
  }
}

@Composable
private fun SelectorViewLayoutActionButtons(
  onDiscardClick: () -> Unit,
  onMoveToTrashClick: () -> Unit,
  selectedPathsSize: Int,
) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    val enabled = selectedPathsSize > 0
    TextButton(
      modifier = Modifier.weight(1f),
      onClick = onDiscardClick,
      enabled = enabled,
    ) {
      Text(stringResource(Res.string.discard))
    }
    Button(
      modifier = Modifier.weight(1f),
      onClick = onMoveToTrashClick,
      enabled = enabled,
    ) {
      Text(stringResource(Res.string.move_to_trash))
    }
  }
}

@Composable
private fun <T : IndexedObjectsGroup> FileListHeader(
  indexedObjectsGroup: T,
  listHeader: (T) -> String,
  onSelectPaths: (Set<Path>) -> Unit,
  selectedPaths: Set<Path>,
) {
  ListHeader(
    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 12.dp, end = 0.dp),
    text = listHeader(indexedObjectsGroup),
    trailingContent = {
      RadioButton(
        selected = indexedObjectsGroup.duplicates.all { it.path in selectedPaths },
        onClick = { onSelectPaths(indexedObjectsGroup.duplicates.map { it.path }.toSet()) },
      )
    },
  )
}

@Composable
@Preview
private fun PreviewIndexedObjectsGroupSelectorViewList() = Previewer {
  IndexedObjectsGroupSelectorView(
    uiState = object : IndexedObjectsGroupSelectorUIState<DuplicateHash> {
      override val indexedObjectsGroups: List<DuplicateHash> = List(10) { baseIndex ->
        DuplicateHash(
          duplicates = List(5) {
            val path = "file.$baseIndex.$it".toPath()
            IndexedObjectImpl(
              path = path,
              parentPath = null,
              sizeBytes = 32,
              fileCategory = FileCategory.DOCUMENT,
              createdDate = localDateTimeNow(),
              modifiedDate = localDateTimeNow(),
            )
          },
          hash = 0L,
          totalSizeBytes = 32 * 6L,
        )
      }
      override val selectedPaths: Set<Path> = setOf(indexedObjectsGroups[0].duplicates[1].path)
      override val moveToTrashProgress: MoveToTrashProgress? = null
      override val isGalleryViewEnabled: Boolean = false
    },
    title = "Title",
    navigateUp = {},
    onGalleryViewStateChange = {},
    onSelectPath = {},
    onSelectPaths = {},
    onMoveToTrashStart = {},
    onMoveToTrashCancel = {},
    onDiscard = {},
    listHeader = { formatBytes(it.totalSizeBytes) },
  )
}

@Composable
@Preview
private fun PreviewIndexedObjectsGroupSelectorViewGallery() = Previewer {
  IndexedObjectsGroupSelectorView(
    uiState = object : IndexedObjectsGroupSelectorUIState<DuplicateHash> {
      override val indexedObjectsGroups: List<DuplicateHash> = List(10) { baseIndex ->
        DuplicateHash(
          duplicates = List(5) {
            val path = "file.$baseIndex.$it".toPath()
            IndexedObjectImpl(
              path = path,
              parentPath = null,
              sizeBytes = 32,
              fileCategory = FileCategory.DOCUMENT,
              createdDate = localDateTimeNow(),
              modifiedDate = localDateTimeNow(),
            )
          },
          hash = 0L,
          totalSizeBytes = 32 * 6L,
        )
      }
      override val selectedPaths: Set<Path> = setOf(indexedObjectsGroups[0].duplicates[1].path)
      override val moveToTrashProgress: MoveToTrashProgress? = null
      override val isGalleryViewEnabled: Boolean = true
    },
    title = "Title",
    navigateUp = {},
    onGalleryViewStateChange = {},
    onSelectPath = {},
    onSelectPaths = {},
    onMoveToTrashStart = {},
    onMoveToTrashCancel = {},
    onDiscard = {},
    listHeader = { formatBytes(it.totalSizeBytes) },
  )
}

@Composable
@Preview
private fun PreviewIndexedObjectsFlatSelectorViewList() = Previewer {
  val index = List(10) { baseIndex ->
    val path = "file.$baseIndex.$baseIndex".toPath()
    IndexedObjectImpl(
      path = path,
      parentPath = null,
      sizeBytes = 32,
      fileCategory = FileCategory.DOCUMENT,
      createdDate = localDateTimeNow(),
      modifiedDate = localDateTimeNow(),
    )
  }
  IndexedObjectsFlatSelectorView(
    uiState = IndexedObjectsFlatSelectorUIState(
      indexedObjects = index,
      selectedPaths = index.take(2).map { it.path }.toSet(),
      moveToTrashProgress = null,
      isGalleryViewEnabled = false,
    ),
    title = "Flat list",
    navigateUp = {},
    onGalleryViewStateChange = {},
    onSelectPath = {},
    onMoveToTrashStart = {},
    onMoveToTrashCancel = {},
    onDiscard = {},
  )
}

@Composable
@Preview
private fun PreviewIndexedObjectsFlatSelectorViewGallery() = Previewer {
  val index = List(10) { baseIndex ->
    val path = "file.$baseIndex.$baseIndex".toPath()
    IndexedObjectImpl(
      path = path,
      parentPath = null,
      sizeBytes = 32,
      fileCategory = FileCategory.DOCUMENT,
      createdDate = localDateTimeNow(),
      modifiedDate = localDateTimeNow(),
    )
  }
  IndexedObjectsFlatSelectorView(
    uiState = IndexedObjectsFlatSelectorUIState(
      indexedObjects = index,
      selectedPaths = index.take(2).map { it.path }.toSet(),
      moveToTrashProgress = null,
      isGalleryViewEnabled = true,
    ),
    title = "Flat list",
    navigateUp = {},
    onGalleryViewStateChange = {},
    onSelectPath = {},
    onMoveToTrashStart = {},
    onMoveToTrashCancel = {},
    onDiscard = {},
  )
}
