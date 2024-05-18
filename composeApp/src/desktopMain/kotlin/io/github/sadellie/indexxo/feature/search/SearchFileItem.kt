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

package io.github.sadellie.indexxo.feature.search

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.open
import indexxo.composeapp.generated.resources.show_in_explorer
import io.github.sadellie.indexxo.core.common.localDateTimeNow
import io.github.sadellie.indexxo.core.designsystem.component.FileThumbnail
import io.github.sadellie.indexxo.core.designsystem.component.ListItem
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObject
import io.github.sadellie.indexxo.core.model.IndexedObjectImpl
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchFileItem(
  modifier: Modifier = Modifier,
  item: IndexedObject,
  optionMenuOnOpen: () -> Unit,
  optionMenuOnShowInExplorer: () -> Unit,
) {
  val contextMenuItems = rememberIndexedObjectInSearchContextMenuItems(
    onOpen = optionMenuOnOpen,
    onShowInExplorer = optionMenuOnShowInExplorer,
  )

  ContextMenuArea(items = { contextMenuItems }) {
    ListItem(
      modifier = modifier.clip(RoundedCornerShape(16.dp)).clickable { optionMenuOnOpen() },
      leadingContent = {
        FileThumbnail(
          modifier = Modifier.padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .size(48.dp),
          item = item,
        )
      },
      headlineContent = { Text(item.path.name) },
      supportingContent = {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
          val formattedCreatedDate =
            remember(item.createdDate) { commonLocalDateTimeFormat.format(item.createdDate) }

          Text(text = item.path.toString(), modifier = Modifier.weight(1f))
          Text(text = formattedCreatedDate)
        }
      },
    )
  }
}

@Composable
private fun rememberIndexedObjectInSearchContextMenuItems(
  onOpen: () -> Unit,
  onShowInExplorer: () -> Unit,
): List<ContextMenuItem> {
  val openString = stringResource(Res.string.open)
  val showInExplorerString = stringResource(Res.string.show_in_explorer)
  return remember(onOpen, onShowInExplorer) {
    listOf(
      ContextMenuItem(openString, onOpen),
      ContextMenuItem(showInExplorerString, onShowInExplorer),
    )
  }
}

@Composable
@Preview
private fun PreviewSearchFileItem() {
  SearchFileItem(
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
    optionMenuOnOpen = {},
    optionMenuOnShowInExplorer = {},
  )
}
