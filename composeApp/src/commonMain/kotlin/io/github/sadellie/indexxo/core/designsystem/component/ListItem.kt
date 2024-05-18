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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import io.github.sadellie.indexxo.core.common.toPercent
import io.github.sadellie.indexxo.core.designsystem.ProvideColor
import io.github.sadellie.indexxo.core.designsystem.ProvideStyle
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Settings
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded
import kotlin.math.roundToInt

@Composable
fun ListItem(
  modifier: Modifier = Modifier,
  headlineContent: @Composable () -> Unit,
  supportingContent: @Composable (() -> Unit)? = null,
  leadingContent: @Composable (() -> Unit)? = null,
  trailingContent: @Composable (() -> Unit)? = null,
) {
  Row(
    modifier =
      modifier
        .padding(start = 16.dp, end = 24.dp, top = 8.dp, bottom = 8.dp)
        .heightIn(min = if (supportingContent == null) 40.dp else 56.dp),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    leadingContent?.let {
      ProvideColor(color = MaterialTheme.colorScheme.onSurfaceVariant, content = it)
    }

    Column(Modifier.weight(1f)) {
      ProvideStyle(
        color = MaterialTheme.colorScheme.onSurface,
        textStyle = MaterialTheme.typography.bodyLarge,
        content = headlineContent,
      )
      supportingContent?.let {
        ProvideStyle(
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textStyle = MaterialTheme.typography.bodyMedium,
          content = it,
        )
      }
    }
    trailingContent?.let {
      ProvideColor(color = MaterialTheme.colorScheme.onSurfaceVariant, content = it)
    }
  }
}

@Composable
fun ListItem(
  modifier: Modifier = Modifier,
  headlineText: String,
  supportingText: String? = null,
  leadingContent: @Composable (() -> Unit)? = null,
  trailingContent: @Composable (() -> Unit)? = null,
) =
  ListItem(
    modifier = modifier,
    headlineContent = { Text(headlineText) },
    supportingContent = supportingText?.let { { Text(it) } },
    leadingContent = leadingContent,
    trailingContent = trailingContent,
  )

@Composable
fun ListItem(
  modifier: Modifier = Modifier,
  headlineText: String,
  supportingText: String? = null,
  icon: ImageVector,
  iconDescription: String = headlineText,
  trailing: @Composable (() -> Unit)? = null,
) =
  ListItem(
    modifier = modifier,
    headlineText = headlineText,
    supportingText = supportingText,
    leadingContent = {
      Icon(
        imageVector = icon,
        contentDescription = iconDescription,
        modifier = Modifier.size(24.dp),
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    },
    trailingContent = trailing,
  )

@Composable
fun ListItem(
  modifier: Modifier = Modifier,
  headlineText: String,
  icon: ImageVector,
  iconDescription: String = headlineText,
  supportingText: String? = null,
  switchState: Boolean,
  onSwitchChange: (Boolean) -> Unit,
) =
  ListItem(
    modifier =
      modifier.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(),
        onClick = { onSwitchChange(!switchState) },
        role = Role.Switch,
      ),
    headlineText = headlineText,
    supportingText = supportingText,
    icon = icon,
    iconDescription = iconDescription,
    trailing = { Switch(checked = switchState, onCheckedChange = { onSwitchChange(it) }) },
  )

@Composable
fun ListItem(
  modifier: Modifier = Modifier,
  headlineText: String,
  supportingText: String? = null,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  onAdditionalContentClick: (() -> Unit)? = null,
) =
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier.height(IntrinsicSize.Max),
  ) {
    ListItem(
      modifier = Modifier.clickable { onCheckedChange(!checked) }.weight(1f),
      headlineText = headlineText,
      supportingText = supportingText,
      leadingContent = {
        Checkbox(
          checked = checked,
          onCheckedChange = onCheckedChange,
          modifier = Modifier.size(24.dp),
        )
      },
    )

    if (onAdditionalContentClick != null) {
      Row(
        modifier = Modifier.clickable { onAdditionalContentClick() }.fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        VerticalDivider(Modifier.height(32.dp))
        IconButton(
          onClick = onAdditionalContentClick,
          modifier = Modifier.padding(horizontal = 16.dp),
        ) {
          Icon(
            imageVector = SymbolsRounded.Settings,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
          )
        }
      }
    }
  }

@Composable
fun ListItem(
  modifier: Modifier = Modifier,
  headlineText: String,
  supportingText: String? = null,
  selected: Boolean,
  onSelect: () -> Unit,
  trailingContent: @Composable (() -> Unit)? = null,
) =
  ListItem(
    modifier = Modifier.clickable(onClick = onSelect).then(modifier),
    headlineText = headlineText,
    supportingText = supportingText,
    leadingContent = {
      RadioButton(modifier = Modifier.size(24.dp), selected = selected, onClick = onSelect)
    },
    trailingContent = trailingContent,
  )

@Composable
fun ListItemWithSlider(
  headlineText: String,
  supportingContent: @Composable (() -> Unit)? = null,
  leadingContent: @Composable (() -> Unit)? = null,
  initialValue: Float,
  onValueChange: (Float) -> Unit,
  formatValueLabel: (Float) -> String = Float::toPercent,
  valueRange: ClosedFloatingPointRange<Float>,
  steps: Int = countStepsForValueRange(valueRange)
) {
  var currentValue by remember(initialValue) { mutableStateOf(initialValue) }

  ListItem(
    leadingContent = leadingContent,
    headlineContent = {
      Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(headlineText)
        Text(formatValueLabel(currentValue))
      }
    },
    supportingContent = supportingContent
  )

  Slider(
    modifier = Modifier.padding(start = 56.dp, end = 16.dp),
    value = currentValue,
    onValueChange = { currentValue = it },
    onValueChangeFinished = { onValueChange(currentValue) },
    valueRange = valueRange,
    steps = steps
  )
}

private fun countStepsForValueRange(valueRange: ClosedFloatingPointRange<Float>): Int =
  ((valueRange.endInclusive - valueRange.start) * 100).roundToInt() - 1
