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

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.app_name
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Settings
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded
import io.github.sadellie.indexxo.core.designsystem.theme.Previewer
import org.jetbrains.compose.resources.stringResource

@Composable
fun FrameWindowScope.LinuxWindowFrame(
  onCloseRequest: () -> Unit,
  onSettingsClick: () -> Unit,
  title: String,
  state: WindowState,
  content: @Composable (FrameWindowScope.() -> Unit),
) {
  Column {
    WindowDraggableArea {
      LinuxWindowFrameToolbar(
        modifier = Modifier.fillMaxWidth(),
        title = title,
        isMaximized = state.placement == WindowPlacement.Maximized,
        onMinimize = { state.isMinimized = true },
        onMaximize = { state.placement = WindowPlacement.Maximized },
        onRestore = { state.placement = WindowPlacement.Floating },
        onClose = onCloseRequest,
        onSettingsClick = onSettingsClick,
      )
    }

    HorizontalDivider()

    content()
  }
}

@Composable
fun DialogWindowScope.LinuxDialogWindowFrame(
  onCloseRequest: () -> Unit,
  title: String,
  content: @Composable (DialogWindowScope.() -> Unit),
) {
  Column(
    modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    WindowDraggableArea {
      LinuxDialogWindowFrameToolbar(
        modifier = Modifier,
        title = title,
        onClose = onCloseRequest,
      )
    }

    HorizontalDivider()

    content()
  }
}

@Composable
private fun LinuxDialogWindowFrameToolbar(
  modifier: Modifier,
  title: String,
  onClose: () -> Unit,
) {
  Row(
    modifier = modifier
      .background(MaterialTheme.colorScheme.background)
      .requiredHeight(ControlBarHeight),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.End,
  ) {
    val toolBarContentColor = toolbarContentColor()
    CompositionLocalProvider(LocalContentColor provides toolBarContentColor) {
      Text(
        text = title,
        modifier = Modifier.weight(1f),
        textAlign = TextAlign.Center,
        maxLines = 1,
      )

      Spacer(Modifier.width(8.dp))

      Row(
        modifier = Modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        CompositionLocalProvider(LocalIndication provides CloseControlButtonIndication) {
          LinuxWindowControlButton(
            painter = painterResource("icons/controls/close.svg"),
            onClick = onClose,
          )
        }
      }
    }
  }
}

@Composable
private fun LinuxWindowFrameToolbar(
  modifier: Modifier,
  title: String,
  isMaximized: Boolean,
  onMinimize: () -> Unit,
  onMaximize: () -> Unit,
  onRestore: () -> Unit,
  onClose: () -> Unit,
  onSettingsClick: () -> Unit,
) {
  Row(
    modifier = modifier
      .background(MaterialTheme.colorScheme.background)
      .requiredHeight(ControlBarHeight),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.End,
  ) {
    val toolBarContentColor = toolbarContentColor()
    CompositionLocalProvider(LocalContentColor provides toolBarContentColor) {
      Text(
        text = title,
        modifier = Modifier.weight(1f),
        textAlign = TextAlign.Center,
        maxLines = 1,
      )

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(4.dp))
          .background(MaterialTheme.colorScheme.secondaryContainer)
          .clickable { onSettingsClick() }
          .requiredSize(AdditionalButtonSize),
        contentAlignment = Alignment.Center,
      ) {
        Icon(SymbolsRounded.Settings, null, Modifier.size(AdditionalButtonIconSize))
      }

      Spacer(Modifier.width(8.dp))

      Row(
        modifier = Modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        LinuxWindowControlButton(
          painter = painterResource("icons/controls/minimize.svg"),
          onClick = onMinimize,
        )

        if (isMaximized) {
          LinuxWindowControlButton(
            painter = painterResource("icons/controls/restore.svg"),
            onClick = onRestore,
          )
        } else {
          LinuxWindowControlButton(
            painter = painterResource("icons/controls/maximize.svg"),
            onClick = onMaximize,
          )
        }

        CompositionLocalProvider(LocalIndication provides CloseControlButtonIndication) {
          LinuxWindowControlButton(
            painter = painterResource("icons/controls/close.svg"),
            onClick = onClose,
          )
        }
      }
    }
  }
}

@Composable
private fun LinuxWindowControlButton(
  modifier: Modifier = Modifier,
  painter: Painter,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .clip(CircleShape)
      .background(MaterialTheme.colorScheme.secondaryContainer)
      .clickable { onClick() }
      .requiredSize(ControlButtonSize),
    contentAlignment = Alignment.Center,
  ) {
    Icon(painter, null)
  }
}

private val ControlBarHeight = 46.dp
private val ControlButtonSize = 24.dp
private val AdditionalButtonSize = 30.dp
private val AdditionalButtonIconSize = 20.dp

@Composable
@Preview
private fun PreviewLinuxWindowFrameToolbar() = Previewer {
  Column {
    LinuxWindowFrameToolbar(
      modifier = Modifier,
      title = stringResource(Res.string.app_name),
      isMaximized = false,
      onMinimize = {},
      onMaximize = {},
      onRestore = {},
      onClose = {},
      onSettingsClick = {},
    )

    HorizontalDivider()

    LinuxWindowFrameToolbar(
      modifier = Modifier,
      title = stringResource(Res.string.app_name),
      isMaximized = true,
      onMinimize = {},
      onMaximize = {},
      onRestore = {},
      onClose = {},
      onSettingsClick = {},
    )

    HorizontalDivider()

    LinuxDialogWindowFrameToolbar(
      modifier = Modifier,
      title = stringResource(Res.string.app_name),
      onClose = {},
    )
  }
}
