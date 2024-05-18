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

package io.github.sadellie.indexxo.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.loading
import indexxo.composeapp.generated.resources.search_empty_results
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.ArrowDownward
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.ArrowUpward
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded
import io.github.sadellie.indexxo.core.designsystem.wResizeCursor
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

data class TableColumnInfo(
  val name: StringResource,
  val width: Dp,
  val minWidth: Dp = 32.dp,
  val draggable: Boolean = false,
  val sortState: TableColumnSort = TableColumnSort.DISABLED,
)

fun List<TableColumnInfo>.getCurrentSorting(): Pair<Int, Boolean> {
  val sortingColumnIndex =
    this.indexOfFirst { tableColumnInfo -> tableColumnInfo.sortState.isActive }
  val isDescending =
    this.getOrNull(sortingColumnIndex)?.sortState == TableColumnSort.ACTIVE_DESCENDING
  return sortingColumnIndex to isDescending
}

enum class TableColumnSort(val isActive: Boolean = false) {
  DISABLED,
  ACTIVE_ASCENDING(true),
  ACTIVE_DESCENDING(true),
  INACTIVE;

  fun shiftNext(): TableColumnSort =
    when (this) {
      DISABLED -> DISABLED
      ACTIVE_ASCENDING -> ACTIVE_DESCENDING
      ACTIVE_DESCENDING -> ACTIVE_ASCENDING
      INACTIVE -> ACTIVE_ASCENDING
    }
}

@Composable
fun <T> Table(
  modifier: Modifier = Modifier,
  items: List<T>?, // Null when loading
  itemKey: ((index: Int, item: T) -> Any)? = null,
  tableColumnInfos: List<TableColumnInfo>,
  onTableColumnInfosChange: ((List<TableColumnInfo>) -> Unit) = {},
  row: @Composable (index: Int, item: T) -> Unit,
  emptyPlaceholder: @Composable () -> Unit = {
    Text(stringResource(Res.string.search_empty_results))
  },
  loadingPlaceholder: @Composable () -> Unit = { Text(stringResource(Res.string.loading)) },
) {
  Column(modifier = modifier) {
    TableHeader(tableColumnInfos, onTableColumnInfosChange)

    HorizontalDivider()

    when {
      items == null -> loadingPlaceholder()
      items.isEmpty() -> emptyPlaceholder()
      else -> {
        val tableListState =
          rememberSaveable(items, saver = LazyListState.Saver) { LazyListState() }
        RowWithScrollbar(listState = tableListState) {
          LazyColumn(
            modifier = Modifier.weight(1f),
            state = tableListState,
            verticalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = PaddingValues(tableContentPadding),
          ) {
            itemsIndexed(items, itemKey) { index, item -> row(index, item) }
          }
        }
      }
    }
  }
}

@Composable
private fun TableHeader(
  tableColumnInfos: List<TableColumnInfo>,
  onTableColumnInfosChange: ((List<TableColumnInfo>) -> Unit) = {},
) {
  Row(modifier = Modifier.padding(horizontal = tableContentPadding)) {
    tableColumnInfos.forEachIndexed { index, tableColumnInfo ->
      var currentWidth by
      remember(tableColumnInfo.width) { mutableStateOf(tableColumnInfo.width) }
      val interactionSource = remember { MutableInteractionSource() }

      TableCell(
        modifier = Modifier.sortController(
          index = index,
          interactionSource = interactionSource,
          tableColumnInfo = tableColumnInfo,
          tableColumnInfos = tableColumnInfos,
          onTableColumnInfosChange = onTableColumnInfosChange,
        )
          .height(IntrinsicSize.Min)
          .width(currentWidth),
        text = stringResource(tableColumnInfo.name),
        trailingContent = {
          TableColumnSortIndicator(tableColumnInfo)

          val density = LocalDensity.current
          val draggableState = rememberDraggableState {
            val change = with(density) { it.toDp() }
            currentWidth = (currentWidth + change).coerceAtLeast(tableColumnInfo.minWidth)
          }
          VerticalDivider(
            modifier = Modifier
              .wResizeCursor()
              .draggable(
                state = draggableState,
                interactionSource = interactionSource,
                orientation = Orientation.Horizontal,
                enabled = tableColumnInfo.draggable,
                onDragStopped = {
                  val newColumnInfos = tableColumnInfos.toMutableList()
                  newColumnInfos[index] = tableColumnInfo.copy(width = currentWidth)
                  onTableColumnInfosChange(newColumnInfos)
                },
              )
              .padding(horizontal = 4.dp),
          )
        },
      )
    }
  }
}

@Composable
private fun TableColumnSortIndicator(tableColumnInfo: TableColumnInfo) {
  when (tableColumnInfo.sortState) {
    TableColumnSort.ACTIVE_ASCENDING ->
      Icon(
        imageVector = SymbolsRounded.ArrowUpward,
        contentDescription = null,
        modifier = Modifier.size(16.dp),
      )

    TableColumnSort.ACTIVE_DESCENDING ->
      Icon(
        imageVector = SymbolsRounded.ArrowDownward,
        contentDescription = null,
        modifier = Modifier.size(16.dp),
      )

    else -> Unit
  }
}

private fun Modifier.sortController(
  index: Int,
  interactionSource: MutableInteractionSource,
  tableColumnInfo: TableColumnInfo,
  tableColumnInfos: List<TableColumnInfo>,
  onTableColumnInfosChange: ((List<TableColumnInfo>) -> Unit) = {},
): Modifier = composed {
  val isDragging = interactionSource.collectIsDraggedAsState()
  val ripple = rememberRipple()

  this
    .clickable(
      interactionSource = interactionSource,
      enabled = tableColumnInfo.sortState != TableColumnSort.DISABLED,
      indication = if (isDragging.value) null else ripple,
    ) {
      val newColumnInfos = tableColumnInfos.toMutableList()
      newColumnInfos.forEachIndexed { index, tableColumnInfo ->
        if (tableColumnInfo.sortState != TableColumnSort.DISABLED) {
          newColumnInfos[index] =
            tableColumnInfo.copy(sortState = TableColumnSort.INACTIVE)
        }
      }
      newColumnInfos[index] =
        tableColumnInfo.copy(sortState = tableColumnInfo.sortState.shiftNext())
      onTableColumnInfosChange(newColumnInfos)
    }
}


@Composable
fun TableCell(
  modifier: Modifier,
  text: String,
  leadingContent: (@Composable RowScope.() -> Unit)? = null,
  trailingContent: (@Composable RowScope.() -> Unit)? = null,
) {
  Row(
    modifier = modifier.padding(vertical = 4.dp).heightIn(min = 32.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    leadingContent?.invoke(this)
    Text(
      text = text,
      minLines = 1,
      maxLines = 1,
      overflow = TextOverflow.Clip,
      softWrap = false,
      style = MaterialTheme.typography.bodySmall,
      modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
    )
    trailingContent?.invoke(this)
  }
}

@Composable
fun TableRow(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) =
  Row(
    modifier = Modifier.clip(RoundedCornerShape(8.dp)).then(modifier),
    verticalAlignment = Alignment.CenterVertically,
    content = content,
  )

private val tableContentPadding = 4.dp
