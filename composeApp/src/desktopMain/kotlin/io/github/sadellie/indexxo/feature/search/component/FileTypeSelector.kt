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

package io.github.sadellie.indexxo.feature.search.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.search_file_type
import io.github.sadellie.indexxo.core.designsystem.component.FilterChip
import io.github.sadellie.indexxo.core.model.FileCategory
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileTypeSelectorSheet(
  sheetState: SheetState,
  onDismissRequest: () -> Unit,
  selectedFileCategories: List<FileCategory>,
  onSelectedFileTypesChange: (List<FileCategory>) -> Unit,
) {
  var currentFileTypes by
    remember(selectedFileCategories) { mutableStateOf(selectedFileCategories) }
  BasicSearchModalSheet(
    title = stringResource(Res.string.search_file_type),
    sheetState = sheetState,
    onDismissRequest = onDismissRequest,
    onConfirm = { onSelectedFileTypesChange(currentFileTypes) },
  ) {
    FileTypeSelector(
      selectedFileCategories = currentFileTypes,
      onSelectedFileTypesChange = { currentFileTypes = it },
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FileTypeSelector(
  selectedFileCategories: List<FileCategory>,
  onSelectedFileTypesChange: (List<FileCategory>) -> Unit,
) {
  FlowRow(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    FileCategory.entries.forEach { fileType ->
      val selected = fileType in selectedFileCategories
      FilterChip(
        selected = selected,
        onClick = {
          val newList =
            if (selected) {
              selectedFileCategories - fileType
            } else {
              selectedFileCategories + fileType
            }

          onSelectedFileTypesChange(newList)
        },
        label = stringResource(fileType.res),
      )
    }
  }
}

@Composable
@Preview
private fun PreviewFileTypeSelector() {
  FileTypeSelector(
    selectedFileCategories = FileCategory.entries.take(3),
    onSelectedFileTypesChange = {},
  )
}
