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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Basic list header.
 *
 * @param text Group label.
 * @param modifier Modifier that will be applied to Text composable.
 */
@Composable
fun ListHeader(
  text: String,
  trailingContent: (@Composable () -> Unit)? = null,
  modifier: Modifier = Modifier.padding(start = 56.dp, end = 16.dp, top = 24.dp, bottom = 12.dp),
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      modifier = Modifier.weight(1f),
      text = text,
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.primary,
    )
    trailingContent?.invoke()
  }
}
