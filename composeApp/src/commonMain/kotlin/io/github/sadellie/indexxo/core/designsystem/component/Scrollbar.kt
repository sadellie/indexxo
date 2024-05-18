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

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RowWithScrollbar(
  modifier: Modifier = Modifier,
  listState: LazyListState,
  content: @Composable RowScope.() -> Unit,
) {
  Row(modifier = modifier) {
    content()
    VerticalScrollbar(listState)
  }
}

@Composable
fun RowWithScrollbar(
  modifier: Modifier = Modifier,
  gridState: LazyGridState,
  content: @Composable RowScope.() -> Unit,
) {
  Row(modifier = modifier) {
    content()
    VerticalScrollbar(gridState)
  }
}

@Composable
fun RowWithScrollbar(
  modifier: Modifier = Modifier,
  scrollState: ScrollState,
  horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
  verticalAlignment: Alignment.Vertical = Alignment.Top,
  content: @Composable RowScope.() -> Unit,
) {
  Row(
    modifier = modifier,
    horizontalArrangement = horizontalArrangement,
    verticalAlignment = verticalAlignment,
  ) {
    content()
    VerticalScrollbar(scrollState)
  }
}

@Composable
fun ColumnWithScrollbar(
  modifier: Modifier = Modifier,
  listState: LazyListState,
  content: @Composable ColumnScope.() -> Unit,
) {
  Column(modifier = modifier) {
    content()
    HorizontalScrollbar(listState)
  }
}

@Composable
private fun VerticalScrollbar(state: LazyListState) {
  if (state.canScrollForward or state.canScrollBackward) {
    androidx.compose.foundation.VerticalScrollbar(
      modifier = Modifier.fillMaxHeight().padding(DefaultScrollbarPadding),
      adapter = rememberScrollbarAdapter(state),
    )
  }
}

@Composable
fun HorizontalScrollbar(state: LazyListState) {
  if (state.canScrollForward or state.canScrollBackward) {
    androidx.compose.foundation.HorizontalScrollbar(
      modifier = Modifier.fillMaxHeight().padding(DefaultScrollbarPadding),
      adapter = rememberScrollbarAdapter(state),
    )
  }
}

@Composable
private fun VerticalScrollbar(state: LazyGridState) {
  if (state.canScrollForward or state.canScrollBackward) {
    androidx.compose.foundation.VerticalScrollbar(
      modifier = Modifier.fillMaxHeight().padding(DefaultScrollbarPadding),
      adapter = rememberScrollbarAdapter(state),
    )
  }
}

@Composable
private fun VerticalScrollbar(state: ScrollState) {
  if (state.canScrollForward or state.canScrollBackward) {
    androidx.compose.foundation.VerticalScrollbar(
      modifier = Modifier.fillMaxHeight().padding(DefaultScrollbarPadding),
      adapter = rememberScrollbarAdapter(state),
    )
  }
}

private val DefaultScrollbarPadding = 2.dp
