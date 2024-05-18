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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Template screen. Uses [Scaffold] and [TopAppBar]
 *
 * @param modifier See [Scaffold]
 * @param title See [TopAppBar]
 * @param navigationIcon See [TopAppBar]
 * @param actions See [TopAppBar]
 * @param colors See [TopAppBar]
 * @param containerColor See [Scaffold]
 * @param content See [Scaffold]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithTopBar(
  modifier: Modifier = Modifier,
  title: String,
  navigationIcon: @Composable () -> Unit = {},
  actions: @Composable RowScope.() -> Unit = {},
  colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
  containerColor: Color = MaterialTheme.colorScheme.background,
  content: @Composable (PaddingValues) -> Unit,
) {
  Scaffold(
    modifier = modifier,
    topBar = {
      TopAppBar(
        title = { Text(title) },
        navigationIcon = navigationIcon,
        actions = actions,
        colors = colors,
      )
    },
    containerColor = containerColor,
    content = content,
  )
}
