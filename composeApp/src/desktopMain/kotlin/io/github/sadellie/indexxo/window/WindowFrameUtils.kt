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

package io.github.sadellie.indexxo.window

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.platform.LocalWindowInfo

@Composable
internal fun toolbarContentColor(): Color =
  if (LocalWindowInfo.current.isWindowFocused) {
    MaterialTheme.colorScheme.onBackground
  } else {
    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
  }

internal object CloseControlButtonIndication : Indication {
  private class CloseControlButtonIndicationInstance(
    private val isPressed: State<Boolean>,
    private val isHovered: State<Boolean>,
    private val isFocused: State<Boolean>,
  ) : IndicationInstance {
    override fun ContentDrawScope.drawIndication() {
      when {
        isPressed.value -> drawRect(color = CloseButtonPressedBackground, size = size)
        isHovered.value or isFocused.value -> drawRect(CloseButtonHoveredBackground, size = size)
      }
      drawContent()
    }
  }

  @Composable
  override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
    val isPressed = interactionSource.collectIsPressedAsState()
    val isHovered = interactionSource.collectIsHoveredAsState()
    val isFocused = interactionSource.collectIsFocusedAsState()
    return remember(interactionSource) {
      CloseControlButtonIndicationInstance(isPressed, isHovered, isFocused)
    }
  }
}

private val CloseButtonPressedBackground = Color(0xFF9C1C27)
private val CloseButtonHoveredBackground = Color(0xFFE81123)
