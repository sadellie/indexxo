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

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.open
import indexxo.composeapp.generated.resources.select
import indexxo.composeapp.generated.resources.show_in_explorer
import io.github.sadellie.indexxo.core.designsystem.component.FileThumbnail
import io.github.sadellie.indexxo.core.designsystem.component.ListItem
import io.github.sadellie.indexxo.core.model.IndexedObject
import org.jetbrains.compose.resources.stringResource

@Composable
fun FileListItem(
  modifier: Modifier = Modifier,
  item: IndexedObject,
  isSelected: Boolean,
  onClick: () -> Unit,
  onOpen: () -> Unit,
  onShowInExplorer: () -> Unit,
) {
  val contextMenuItems =
    rememberIndexedObjectInListContextMenuItems(
      onSelect = onClick,
      onOpen = onOpen,
      onShowInExplorer = onShowInExplorer,
    )

  ContextMenuArea(items = { contextMenuItems }) {
    BasicFileListItem(modifier, item, isSelected, onClick)
  }
}

@Composable
fun FileGridItem(
  modifier: Modifier = Modifier,
  item: IndexedObject,
  isSelected: Boolean,
  onClick: () -> Unit,
  onOpen: () -> Unit,
  onShowInExplorer: () -> Unit,
) {
  val contextMenuItems =
    rememberIndexedObjectInListContextMenuItems(
      onSelect = onClick,
      onOpen = onOpen,
      onShowInExplorer = onShowInExplorer,
    )

  ContextMenuArea(items = { contextMenuItems }) {
    BasicFileGridItem(modifier, item, isSelected, onClick)
  }
}

@Composable
fun rememberIndexedObjectInListContextMenuItems(
  onSelect: () -> Unit,
  onOpen: () -> Unit,
  onShowInExplorer: () -> Unit,
): List<ContextMenuItem> {
  val selectString = stringResource(Res.string.select)
  val openString = stringResource(Res.string.open)
  val showInExplorerString = stringResource(Res.string.show_in_explorer)
  return remember(onSelect, onOpen, onShowInExplorer) {
    listOf(
      ContextMenuItem(selectString, onSelect),
      ContextMenuItem(openString, onOpen),
      ContextMenuItem(showInExplorerString, onShowInExplorer),
    )
  }
}

@Composable
private fun BasicFileListItem(
  modifier: Modifier = Modifier,
  item: IndexedObject,
  isSelected: Boolean,
  onClick: () -> Unit,
) {
  val transition = updateTransition(isSelected)
  val backgroundColor = transition.animateColor {
    if (it) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
  }

  ListItem(
    modifier = Modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .then(other = modifier.background(backgroundColor.value)),
    leadingContent = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        RadioButton(selected = isSelected, onClick = onClick)
        FileThumbnail(
          modifier = Modifier.padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .size(48.dp),
          item = item,
        )
      }
    },
    headlineContent = {
      Text(
        text = item.path.name,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    },
    supportingContent = {
      Text(
        text = item.path.toString(),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )
    },
  )
}

@Composable
private fun BasicFileGridItem(
  modifier: Modifier = Modifier,
  item: IndexedObject,
  isSelected: Boolean,
  onClick: () -> Unit,
) {
  val transition = updateTransition(isSelected)
  val backgroundColor =
    transition.animateColor {
      if (it) MaterialTheme.colorScheme.secondaryContainer
      else MaterialTheme.colorScheme.surfaceContainer
    }
  Box(
    modifier = modifier
      .clickable { onClick() }
      .aspectRatio(1f)
      .background(backgroundColor.value)
      .size(96.dp),
  ) {
    CompositionLocalProvider(
      LocalContentColor provides MaterialTheme.colorScheme
        .contentColorFor(backgroundColor.value),
    ) {
      val outerPadding = transition.animateDp { if (it) 8.dp else 0.dp }
      val clipCorner = transition.animateDp { if (it) 16.dp else 0.dp }
      FileThumbnail(
        modifier = Modifier
          .aspectRatio(1f)
          .align(Alignment.Center)
          .padding(outerPadding.value)
          .clip(RoundedCornerShape(clipCorner.value)),
        item = item,
      )

      FileGridTitle(
        modifier = Modifier.align(Alignment.BottomCenter),
        text = item.path.name,
      )

      Row(
        modifier = Modifier.fillMaxWidth()
          .background(
            brush = Brush.verticalGradient(
              0f to Color.Black.copy(alpha = 0.5f),
              1f to Color.Transparent,
            ),
          )
          .align(Alignment.TopCenter),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        RadioButton(
          modifier = Modifier,
          selected = isSelected,
          onClick = onClick,
          colors = RadioButtonDefaults.colors(unselectedColor = Color.White),
        )
      }
    }
  }
}

@Composable
private fun FileGridTitle(
  modifier: Modifier,
  text: String
) {
  Text(
    text = text,
    maxLines = 1,
    style = MaterialTheme.typography.bodySmall,
    color = Color.White,
    textAlign = TextAlign.Center,
    overflow = TextOverflow.Ellipsis,
    modifier = modifier.fillMaxWidth()
      .background(
        brush = Brush.verticalGradient(
          0f to Color.Transparent,
          1f to Color.Black.copy(alpha = 0.5f),
        ),
      )
      .padding(4.dp),
  )
}
