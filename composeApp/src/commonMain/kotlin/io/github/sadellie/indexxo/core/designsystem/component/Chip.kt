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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.ArrowDropDown
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded

@Composable
fun FilterChip(
  modifier: Modifier = Modifier,
  selected: Boolean,
  onClick: () -> Unit,
  label: String,
) {
  BasicChipLayout(
    modifier = modifier,
    selected = selected,
    onClick = onClick,
    label = label,
  )
}

@Composable
fun FilterChip(
  modifier: Modifier = Modifier,
  selected: Boolean,
  onClick: () -> Unit,
  label: String,
  imageVector: ImageVector? = null,
  contentDescription: String? = null,
) {
  BasicChipLayout(
    modifier = modifier,
    selected = selected,
    onClick = onClick,
    label = label,
    leadingContent = {
      imageVector?.let {
        AnimatedVisibility(modifier = Modifier.padding(horizontal = 8.dp), visible = selected) {
          Icon(
            modifier = Modifier.height(FilterChipDefaults.IconSize),
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
          )
        }
      }
    },
  )
}

@Composable
fun DropDownChip(
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
  label: String,
  active: Boolean,
  contentDescription: String? = null,
) {
  BasicChipLayout(
    modifier = modifier,
    selected = active,
    onClick = onClick,
    label = label,
    trailingContent = {
      Icon(
        modifier = Modifier.padding(start = 4.dp, end = 8.dp).height(FilterChipDefaults.IconSize),
        imageVector = SymbolsRounded.ArrowDropDown,
        contentDescription = contentDescription,
        tint = MaterialTheme.colorScheme.onPrimaryContainer,
      )
    },
  )
}

@Composable
fun AssistChip(
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
  imageVector: ImageVector,
  contentDescription: String,
) {
  Row(
    modifier =
      modifier
        .padding(vertical = 8.dp)
        .clip(FilterChipDefaults.shape)
        .clickable { onClick() }
        .background(MaterialTheme.colorScheme.surface)
        .border(
          width = 1.dp,
          color = MaterialTheme.colorScheme.outline,
          shape = AssistChipDefaults.shape,
        )
        .height(32.dp)
        .padding(horizontal = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      modifier = Modifier.height(AssistChipDefaults.IconSize),
      imageVector = imageVector,
      contentDescription = contentDescription,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
fun SuggestionChip(
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null,
  label: String,
  leadingContent: (@Composable () -> Unit)? = null,
  trailingContent: (@Composable () -> Unit)? = null,
) {
  BasicChipLayout(
    modifier = modifier,
    selected = false,
    onClick = onClick,
    label = label,
    leadingContent = leadingContent,
    trailingContent = trailingContent,
  )
}

@Composable
private fun BasicChipLayout(
  modifier: Modifier = Modifier,
  selected: Boolean,
  onClick: (() -> Unit)? = null,
  label: String,
  leadingContent: (@Composable () -> Unit)? = null,
  trailingContent: (@Composable () -> Unit)? = null,
) {
  Row(
    modifier =
      modifier
        .clip(FilterChipDefaults.shape)
        .clickable(enabled = onClick != null) { onClick?.invoke() }
        .background(
          color = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
          shape = FilterChipDefaults.shape,
        )
        .border(
          width = 1.dp,
          color = if (selected) Color.Transparent else MaterialTheme.colorScheme.outline,
          shape = FilterChipDefaults.shape,
        )
        .height(FilterChipDefaults.Height),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    if (leadingContent == null) {
      Spacer(modifier = Modifier.width(16.dp))
    } else {
      leadingContent()
    }

    Text(
      modifier = Modifier,
      text = label,
      style = MaterialTheme.typography.labelLarge,
      color =
        if (selected) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.onSurfaceVariant,
    )

    if (trailingContent == null) {
      Spacer(modifier = Modifier.width(16.dp))
    } else {
      trailingContent()
    }
  }
}
