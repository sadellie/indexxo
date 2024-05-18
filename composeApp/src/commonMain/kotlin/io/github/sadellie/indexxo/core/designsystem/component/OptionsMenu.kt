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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.MoreVert
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded

@Composable
fun OptionsMenuButton(
  modifier: Modifier = Modifier,
  expandedInitially: Boolean = false,
  imageVector: ImageVector = SymbolsRounded.MoreVert,
  content: @Composable (ColumnScope.() -> Unit),
) {
  Box(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
    var expanded by remember(expandedInitially) { mutableStateOf(expandedInitially) }
    IconButton(onClick = { expanded = true }) { Icon(imageVector, null) }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) { content() }
  }
}

@Composable
fun OptionsMenuButton(
  modifier: Modifier = Modifier,
  expanded: Boolean = false,
  onExpand: () -> Unit,
  onCollapse: () -> Unit,
  colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
  content: @Composable (ColumnScope.() -> Unit),
) {
  Box(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
    IconButton(onClick = onExpand, colors = colors) { Icon(SymbolsRounded.MoreVert, null) }
    DropdownMenu(expanded = expanded, onDismissRequest = onCollapse) { content() }
  }
}

@Composable
fun OptionsMenuItem(
  modifier: Modifier = Modifier,
  label: String,
  leadingContent: (@Composable () -> Unit)? = null,
  onClick: () -> Unit,
) {
  ListItem(
    modifier = modifier.clickable { onClick() },
    headlineText = label,
    leadingContent = leadingContent,
  )
}
