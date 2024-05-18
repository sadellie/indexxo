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

package io.github.sadellie.indexxo.feature.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.add
import indexxo.composeapp.generated.resources.edit
import indexxo.composeapp.generated.resources.excluded_tab
import indexxo.composeapp.generated.resources.extension
import indexxo.composeapp.generated.resources.extensions
import indexxo.composeapp.generated.resources.included_tab
import indexxo.composeapp.generated.resources.resume
import indexxo.composeapp.generated.resources.settings_analyzers
import indexxo.composeapp.generated.resources.settings_analyzers_support
import indexxo.composeapp.generated.resources.settings_directories
import indexxo.composeapp.generated.resources.settings_directories_excluded_placeholder
import indexxo.composeapp.generated.resources.settings_directories_included_placeholder
import indexxo.composeapp.generated.resources.settings_extensions_excluded_placeholder
import indexxo.composeapp.generated.resources.settings_extensions_included_placeholder
import indexxo.composeapp.generated.resources.settings_extensions_prefix
import indexxo.composeapp.generated.resources.settings_files
import indexxo.composeapp.generated.resources.settings_files_excluded_placeholder
import indexxo.composeapp.generated.resources.settings_files_included_placeholder
import indexxo.composeapp.generated.resources.settings_max_threads
import indexxo.composeapp.generated.resources.settings_max_threads_support
import indexxo.composeapp.generated.resources.settings_resume_dialog_body
import indexxo.composeapp.generated.resources.settings_resume_dialog_title
import indexxo.composeapp.generated.resources.settings_start
import io.github.sadellie.indexxo.core.common.maxSystemThreads
import io.github.sadellie.indexxo.core.database.model.UserPreset
import io.github.sadellie.indexxo.core.database.model.UserPresetExtension
import io.github.sadellie.indexxo.core.database.model.UserPresetPath
import io.github.sadellie.indexxo.core.designsystem.LocalFileKitPlatformSettings
import io.github.sadellie.indexxo.core.designsystem.component.EmptyScreen
import io.github.sadellie.indexxo.core.designsystem.component.ListItem
import io.github.sadellie.indexxo.core.designsystem.component.RowWithScrollbar
import io.github.sadellie.indexxo.core.designsystem.component.ScaffoldWithTopBar
import io.github.sadellie.indexxo.core.designsystem.component.SimpleAlertDialogConfirmation
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Add
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Architecture
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Close
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SettingsAccountBox
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Visibility
import io.github.sadellie.indexxo.core.designsystem.tableBorders
import io.github.sadellie.indexxo.feature.AnalyzersScreen
import io.github.sadellie.indexxo.feature.home.HomeScreen
import io.github.sadellie.indexxo.feature.loader.LoaderScreen
import io.github.sadellie.indexxo.feature.userpresets.UserPresetsScreen
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.FileKitPlatformSettings
import io.github.vinceglb.filekit.core.PickerMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.Path
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

data object SettingsScreen : Screen {
  @Composable
  override fun Content() {
    val localNavigator = LocalNavigator.currentOrThrow
    val screenModel = getSettingsScreenModel()
    val uiState = screenModel.uiState.collectAsState()

    when (val uiStateValue = uiState.value) {
      SettingsUIState.Loading -> EmptyScreen()

      SettingsUIState.PresetNotFound -> localNavigator.replaceAll(UserPresetsScreen)

      is SettingsUIState.Ready -> SettingsViewReady(
        uiState = uiStateValue,
        addUserPresetPath = screenModel::addUserPresetPath,
        editUserPresetPath = screenModel::editUserPresetPath,
        removeUserPresetPath = screenModel::removeUserPresetPath,
        addExtension = screenModel::addExtension,
        editExtension = screenModel::editExtension,
        removeExtension = screenModel::removeExtension,
        updateDialogState = screenModel::updateDialogState,
        updateUserPreset = screenModel::updateUserPreset,
        onPresetClick = { localNavigator.replaceAll(UserPresetsScreen) },
        onAnalyzersSettingsClick = { localNavigator.push(AnalyzersScreen(it)) },
        onResume = { localNavigator.replaceAll(HomeScreen) },
        onStart = { localNavigator.push(LoaderScreen(it)) },
      )
    }
  }
}

@Composable
expect fun Screen.getSettingsScreenModel(): SettingsScreenModel

expect class SettingsScreenModel: ScreenModel {
  val uiState: StateFlow<SettingsUIState>
  fun updateUserPreset(userPreset: UserPreset)
  fun addUserPresetPath(path: Path, included: Boolean, isDirectory: Boolean, presetId: Int)
  fun editUserPresetPath(id: Int, path: Path)
  fun removeUserPresetPath(id: Int)
  fun addExtension(extension: String, included: Boolean, presetId: Int)
  fun editExtension(id: Int, extension: String)
  fun removeExtension(id: Int)
  fun updateDialogState(dialogState: PathsAndExtensionsDialogState?)
}

sealed class SettingsUIState {
  data object PresetNotFound : SettingsUIState()

  data object Loading : SettingsUIState()

  data class Ready(
    val preset: UserPreset,
    val dialogState: PathsAndExtensionsDialogState?,
    val enableStartButton: Boolean,
    val showResumeButton: Boolean,
    val analyzersCount: Int,
  ) :
    SettingsUIState()
}

sealed class PathsAndExtensionsDialogState {
  data object AddExtension : PathsAndExtensionsDialogState()
  data class EditExtension(val extension: UserPresetExtension) : PathsAndExtensionsDialogState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsViewReady(
  uiState: SettingsUIState.Ready,
  addUserPresetPath: (path: Path, included: Boolean, isDirectory: Boolean, presetId: Int) -> Unit,
  editUserPresetPath: (id: Int, path: Path) -> Unit,
  removeUserPresetPath: (id: Int) -> Unit,
  addExtension: (extension: String, included: Boolean, presetId: Int) -> Unit,
  editExtension: (id: Int, newExtension: String) -> Unit,
  removeExtension: (id: Int) -> Unit,
  updateDialogState: (PathsAndExtensionsDialogState?) -> Unit,
  updateUserPreset: (UserPreset) -> Unit,
  onPresetClick: () -> Unit,
  onAnalyzersSettingsClick: (presetId: Int) -> Unit,
  onResume: () -> Unit,
  onStart: (preset: UserPreset) -> Unit,
) {
  ScaffoldWithTopBar(
    title = uiState.preset.name,
    actions = {
      IconButton(onPresetClick) {
        Icon(SymbolsRounded.SettingsAccountBox, null)
      }
    },
  ) { paddingValues ->
    Column(modifier = Modifier.padding(paddingValues)) {
      var showResumeWithoutReindexDialog by rememberSaveable { mutableStateOf(false) }

      PathsAndExtensionsTable(
        modifier = Modifier.weight(1f),
        uiState = uiState,
        addUserPresetPath = addUserPresetPath,
        editUserPresetPath = editUserPresetPath,
        removeUserPresetPath = removeUserPresetPath,
        addExtension = addExtension,
        editExtension = editExtension,
        removeExtension = removeExtension,
        updateDialogState = updateDialogState,
      )

      AnalyzersListItem(onAnalyzersSettingsClick, uiState)

      MaxThreadsListItem(
        maxThreads = uiState.preset.maxThreads,
        updateMaxThreads = { updateUserPreset(uiState.preset.copy(maxThreads = it)) },
      )

      Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        if (uiState.showResumeButton) {
          TextButton(
            onClick = { showResumeWithoutReindexDialog = true },
            modifier = Modifier.weight(1f),
          ) {
            Text(stringResource(Res.string.resume))
          }
        }

        Button(
          modifier = Modifier.weight(1f),
          onClick = { onStart(uiState.preset) },
          enabled = uiState.enableStartButton,
        ) {
          Text(stringResource(Res.string.settings_start))
        }
      }

      if (showResumeWithoutReindexDialog) {
        ResumeWithoutReindexDialog(
          onResume = onResume,
          onDismissRequest = { showResumeWithoutReindexDialog = false },
        )
      }
    }
  }
}

@Composable
private fun ResumeWithoutReindexDialog(onResume: () -> Unit, onDismissRequest: () -> Unit) {
  SimpleAlertDialogConfirmation(
    title = stringResource(Res.string.settings_resume_dialog_title),
    text = { Text(stringResource(Res.string.settings_resume_dialog_body)) },
    onConfirm = onResume,
    onDismissRequest = onDismissRequest,
  )
}

@Composable
private fun MaxThreadsListItem(
  maxThreads: Int,
  updateMaxThreads: (Int) -> Unit,
) {
  var currentMaxThreads by remember(maxThreads) { mutableStateOf(maxThreads.toFloat()) }

  ListItem(
    leadingContent = { Icon(SymbolsRounded.Architecture, null) },
    headlineContent = {
      Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(stringResource(Res.string.settings_max_threads))
        Text(currentMaxThreads.roundToInt().toString())
      }
    },
    supportingContent = {
      Text(stringResource(Res.string.settings_max_threads_support))
    },
  )

  Slider(
    modifier = Modifier.padding(start = 56.dp, end = 16.dp),
    value = currentMaxThreads,
    onValueChange = { currentMaxThreads = it },
    onValueChangeFinished = {
      updateMaxThreads(currentMaxThreads.roundToInt())
    },
    valueRange = 1f..maxSystemThreads.toFloat(),
    steps = (maxSystemThreads - 2).coerceAtLeast(0),
  )
}

@Composable
private fun AnalyzersListItem(
  onAnalyzersSettingsClick: (presetId: Int) -> Unit,
  uiState: SettingsUIState.Ready
) {
  ListItem(
    modifier = Modifier.clickable { onAnalyzersSettingsClick(uiState.preset.id) },
    headlineContent = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Text(stringResource(Res.string.settings_analyzers))
        Badge { Text(uiState.analyzersCount.toString()) }
      }
    },
    supportingContent = { Text(stringResource(Res.string.settings_analyzers_support)) },
    leadingContent = { Icon(SymbolsRounded.Visibility, null) },
  )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PathsAndExtensionsTable(
  modifier: Modifier,
  uiState: SettingsUIState.Ready,
  addUserPresetPath: (path: Path, included: Boolean, isDirectory: Boolean, presetId: Int) -> Unit,
  editUserPresetPath: (id: Int, path: Path) -> Unit,
  removeUserPresetPath: (id: Int) -> Unit,
  addExtension: (extension: String, included: Boolean, presetId: Int) -> Unit,
  editExtension: (id: Int, newExtension: String) -> Unit,
  removeExtension: (id: Int) -> Unit,
  updateDialogState: (PathsAndExtensionsDialogState?) -> Unit
) {
  Column(
    modifier = modifier.tableBorders(),
  ) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState { 2 }

    PathsAndExtensionsTableHeader(
      pagerState = pagerState,
      onPageClick = { coroutineScope.launch { pagerState.animateScrollToPage(it) } },
    )
    HorizontalPager(
      state = pagerState,
      verticalAlignment = Alignment.Top,
    ) { page ->
      when (page) {
        0 -> PathsAndExtensionsTableContent(
          directories = uiState.preset.includedDirectories,
          files = uiState.preset.includedFiles,
          addUserPresetPath = { path, isDirectory ->
            addUserPresetPath(path, true, isDirectory, uiState.preset.id)
          },
          editUserPresetPath = editUserPresetPath,
          removeUserPresetPath = removeUserPresetPath,
          extensions = uiState.preset.includedExtensions,
          addExtension = { addExtension(it, true, uiState.preset.id) },
          editExtension = editExtension,
          removeExtension = removeExtension,
          dialogState = uiState.dialogState,
          updateDialogState = updateDialogState,
          emptyDirectoriesPlaceholder = stringResource(Res.string.settings_directories_included_placeholder),
          emptyFilesPlaceholder = stringResource(Res.string.settings_files_included_placeholder),
          emptyExtensionsPlaceholder = stringResource(Res.string.settings_extensions_included_placeholder),
        )

        1 -> PathsAndExtensionsTableContent(
          directories = uiState.preset.excludedDirectories,
          files = uiState.preset.excludedFiles,
          addUserPresetPath = { path, isDirectory ->
            addUserPresetPath(path, false, isDirectory, uiState.preset.id)
          },
          editUserPresetPath = editUserPresetPath,
          removeUserPresetPath = removeUserPresetPath,
          extensions = uiState.preset.excludedExtensions,
          addExtension = { addExtension(it, false, uiState.preset.id) },
          editExtension = editExtension,
          removeExtension = removeExtension,
          dialogState = uiState.dialogState,
          updateDialogState = updateDialogState,
          emptyDirectoriesPlaceholder = stringResource(Res.string.settings_directories_excluded_placeholder),
          emptyFilesPlaceholder = stringResource(Res.string.settings_files_excluded_placeholder),
          emptyExtensionsPlaceholder = stringResource(Res.string.settings_extensions_excluded_placeholder),
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun PathsAndExtensionsTableHeader(pagerState: PagerState, onPageClick: (Int) -> Unit) {
  val tabs = listOf(Res.string.included_tab, Res.string.excluded_tab)
  SecondaryTabRow(selectedTabIndex = pagerState.currentPage) {
    tabs.forEachIndexed { index, tab ->
      Tab(
        selected = pagerState.currentPage == index,
        onClick = { onPageClick(index) },
        text = { Text(stringResource(tab)) },
      )
    }
  }
}

@Composable
private fun PathsAndExtensionsTableContent(
  directories: List<UserPresetPath>,
  files: List<UserPresetPath>,
  addUserPresetPath: (path: Path, isDirectory: Boolean) -> Unit,
  editUserPresetPath: (id: Int, newPath: Path) -> Unit,
  removeUserPresetPath: (id: Int) -> Unit,
  extensions: List<UserPresetExtension>,
  addExtension: (extension: String) -> Unit,
  editExtension: (id: Int, newExtension: String) -> Unit,
  removeExtension: (id: Int) -> Unit,
  dialogState: PathsAndExtensionsDialogState?,
  updateDialogState: (PathsAndExtensionsDialogState?) -> Unit,
  emptyDirectoriesPlaceholder: String,
  emptyFilesPlaceholder: String,
  emptyExtensionsPlaceholder: String,
) {
  val listState = rememberLazyListState()

  RowWithScrollbar(
    modifier = Modifier,
    listState = listState,
  ) {
    val coroutineScope = rememberCoroutineScope()
    val fileKitPlatformSettings = LocalFileKitPlatformSettings.current
    LazyColumn(
      state = listState,
      modifier = Modifier.weight(1f),
    ) {
      directoriesTableContent(
        coroutineScope = coroutineScope,
        addUserPresetPath = { addUserPresetPath(it, true) },
        directories = directories,
        emptyDirectoriesPlaceholder = emptyDirectoriesPlaceholder,
        editUserPresetPath = editUserPresetPath,
        removeUserPresetPath = removeUserPresetPath,
        fileKitPlatformSettings = fileKitPlatformSettings,
      )

      filesTableContent(
        coroutineScope = coroutineScope,
        addUserPresetPath = { addUserPresetPath(it, false) },
        files = files,
        emptyFilesPlaceholder = emptyFilesPlaceholder,
        editUserPresetPath = editUserPresetPath,
        removeUserPresetPath = removeUserPresetPath,
        fileKitPlatformSettings = fileKitPlatformSettings,
      )

      extensionsTableContent(
        updateDialogState = updateDialogState,
        extensions = extensions,
        emptyExtensionsPlaceholder = emptyExtensionsPlaceholder,
        removeExtension = removeExtension,
      )
    }
  }

  when (dialogState) {
    PathsAndExtensionsDialogState.AddExtension -> ExtensionAlertDialog(
      title = stringResource(Res.string.add),
      extension = "",
      onConfirm = { addExtension(it) },
      onDismissRequest = { updateDialogState(null) },
    )

    is PathsAndExtensionsDialogState.EditExtension -> ExtensionAlertDialog(
      title = stringResource(Res.string.edit),
      extension = dialogState.extension.extension,
      onConfirm = { editExtension(dialogState.extension.id, it) },
      onDismissRequest = { updateDialogState(null) },
    )

    null -> Unit
  }
}

private fun LazyListScope.directoriesTableContent(
  coroutineScope: CoroutineScope,
  addUserPresetPath: (path: Path) -> Unit,
  directories: List<UserPresetPath>,
  emptyDirectoriesPlaceholder: String,
  editUserPresetPath: (id: Int, newPath: Path) -> Unit,
  removeUserPresetPath: (id: Int) -> Unit,
  fileKitPlatformSettings: FileKitPlatformSettings?
) {
  pathsAndExtensionsHeaderItem(
    key = "directories",
    modifier = Modifier,
    text = Res.string.settings_directories,
    onAdd = {
      coroutineScope.launch {
        pickDirectory(null, fileKitPlatformSettings) { directory ->
          addUserPresetPath(directory)
        }
      }
    },
  )

  if (directories.isEmpty()) {
    pathsAndExtensionsPlaceholderItem(
      key = "directories_placeholder",
      text = emptyDirectoriesPlaceholder,
      modifier = Modifier.fillMaxWidth(),
    )
  } else {
    pathsAndExtensionsItem(
      modifier = Modifier,
      items = directories,
      label = { it.path.toString() },
      key = { it },
      onEdit = {
        coroutineScope.launch {
          pickDirectory(it.path.toString(), fileKitPlatformSettings) { directory ->
            editUserPresetPath(it.id, directory)
          }
        }
      },
      onDelete = { removeUserPresetPath(it.id) },
    )
  }

  item("directories_divider") { HorizontalDivider() }
}

private fun LazyListScope.filesTableContent(
  coroutineScope: CoroutineScope,
  addUserPresetPath: (path: Path) -> Unit,
  files: List<UserPresetPath>,
  emptyFilesPlaceholder: String,
  editUserPresetPath: (id: Int, newPath: Path) -> Unit,
  removeUserPresetPath: (id: Int) -> Unit,
  fileKitPlatformSettings: FileKitPlatformSettings?
) {
  pathsAndExtensionsHeaderItem(
    key = "files",
    modifier = Modifier,
    text = Res.string.settings_files,
    onAdd = {
      coroutineScope.launch {
        pickFile(null, fileKitPlatformSettings) { file ->
          addUserPresetPath(file)
        }
      }
    },
  )

  if (files.isEmpty()) {
    pathsAndExtensionsPlaceholderItem(
      key = "files_placeholder",
      text = emptyFilesPlaceholder,
      modifier = Modifier.fillMaxWidth(),
    )
  } else {
    pathsAndExtensionsItem(
      modifier = Modifier,
      items = files,
      label = { it.path.toString() },
      key = { it },
      onEdit = {
        coroutineScope.launch {
          pickFile(it.path.toString(), fileKitPlatformSettings) { file ->
            editUserPresetPath(it.id, file)
          }
        }
      },
      onDelete = { removeUserPresetPath(it.id) },
    )
  }

  item("files_divider") { HorizontalDivider() }
}

private fun LazyListScope.extensionsTableContent(
  updateDialogState: (PathsAndExtensionsDialogState?) -> Unit,
  extensions: List<UserPresetExtension>,
  emptyExtensionsPlaceholder: String,
  removeExtension: (id: Int) -> Unit
) {
  pathsAndExtensionsHeaderItem(
    key = "extensions",
    modifier = Modifier,
    text = Res.string.extensions,
    onAdd = { updateDialogState(PathsAndExtensionsDialogState.AddExtension) },
  )

  if (extensions.isEmpty()) {
    pathsAndExtensionsPlaceholderItem(
      key = "extensions_placeholder",
      text = emptyExtensionsPlaceholder,
      modifier = Modifier.fillMaxWidth(),
    )
  } else {
    pathsAndExtensionsItem(
      modifier = Modifier,
      items = extensions,
      label = { it.extension },
      key = { it },
      onEdit = { updateDialogState(PathsAndExtensionsDialogState.EditExtension(it)) },
      onDelete = { removeExtension(it.id) },
    )
  }
}

private fun LazyListScope.pathsAndExtensionsHeaderItem(
  key: String,
  modifier: Modifier,
  text: StringResource,
  onAdd: () -> Unit,
) = item(key) {
  Row(
    modifier = modifier.padding(8.dp, 2.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = stringResource(text),
      modifier = Modifier.weight(1f),
      style = MaterialTheme.typography.bodyMedium,
    )
    FilledTonalButton(
      onClick = onAdd,
      contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
    ) {
      Icon(SymbolsRounded.Add, null)
      Spacer(Modifier.width(8.dp))
      Text(stringResource(Res.string.add))
    }
  }
}

private fun <T> LazyListScope.pathsAndExtensionsItem(
  modifier: Modifier,
  items: List<T>,
  label: (T) -> String,
  key: (T) -> Any,
  onEdit: (T) -> Unit,
  onDelete: (T) -> Unit,
) = items(items, key) {
  Row(
    modifier = modifier.clickable { onEdit(it) }.padding(8.dp, 2.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = label(it),
      modifier = Modifier.weight(1f),
    )
    IconButton(onClick = { onDelete(it) }) {
      Icon(SymbolsRounded.Close, null)
    }
  }
}

private fun LazyListScope.pathsAndExtensionsPlaceholderItem(
  key: String,
  modifier: Modifier,
  text: String,
) = item(key) {
  Text(
    modifier = modifier.padding(16.dp),
    text = text,
    textAlign = TextAlign.Center,
    style = MaterialTheme.typography.bodyMedium,
  )
}

@Composable
private fun ExtensionAlertDialog(
  title: String,
  extension: String,
  onConfirm: (String) -> Unit,
  onDismissRequest: () -> Unit,
) {
  var textFieldValue by rememberSaveable(extension) { mutableStateOf(TextFieldValue(extension)) }

  fun confirm() {
    if (textFieldValue.text.isNotBlank()) {
      onConfirm(textFieldValue.text)
      onDismissRequest()
    }
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
        label = { Text(stringResource(Res.string.extension)) },
        singleLine = true,
        visualTransformation = PrefixVisualTransformation(
          stringResource(Res.string.settings_extensions_prefix),
          MaterialTheme.colorScheme.outline,
        ),
      )
    },
    onConfirm = ::confirm,
    onDismissRequest = onDismissRequest,
    confirmEnabled = textFieldValue.text.isNotBlank(),
  )
}

private suspend fun pickDirectory(
  initialDirectory: String?,
  fileKitPlatformSettings: FileKitPlatformSettings?,
  onPicked: (directory: Path) -> Unit
) {
  if (!FileKit.isDirectoryPickerSupported()) return
  val directory = FileKit.pickDirectory(
    initialDirectory = initialDirectory,
    platformSettings = fileKitPlatformSettings,
  )?.path?.toPath()

  if (directory != null) {
    onPicked(directory)
  }
}

private suspend fun pickFile(
  initialDirectory: String?,
  fileKitPlatformSettings: FileKitPlatformSettings?,
  onPicked: (file: Path) -> Unit
) {
  if (!FileKit.isDirectoryPickerSupported()) return
  val file = FileKit.pickFile(
    mode = PickerMode.Single,
    initialDirectory = initialDirectory,
    platformSettings = fileKitPlatformSettings,
  )?.path?.toPath()

  if (file != null) {
    onPicked(file)
  }
}

internal class PrefixVisualTransformation(
  private val prefix: String,
  private val prefixColor: Color
) : VisualTransformation {
  override fun filter(text: AnnotatedString): TransformedText {
    val annotatedString = buildAnnotatedString {
      withStyle(SpanStyle(color = prefixColor)) { append(prefix) }
      append(text)
    }

    return TransformedText(annotatedString, WithStartOffsetMapping())
  }

  inner class WithStartOffsetMapping : OffsetMapping {

    // |ext -> filename.|ext
    override fun originalToTransformed(offset: Int): Int = offset + prefix.length

    // |filename.ext -> |ext
    override fun transformedToOriginal(offset: Int): Int = (offset - prefix.length).coerceAtLeast(0)
  }
}
