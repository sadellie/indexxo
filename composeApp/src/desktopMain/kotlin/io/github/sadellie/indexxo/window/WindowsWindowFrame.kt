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
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import com.mayakapps.compose.windowstyler.WindowBackdrop
import com.mayakapps.compose.windowstyler.WindowStyle
import com.mayakapps.compose.windowstyler.findSkiaLayer
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinUser
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.app_name
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Settings
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded
import io.github.sadellie.indexxo.core.designsystem.theme.Previewer
import io.github.sadellie.indexxo.window.WinUserConst.HTCAPTION
import io.github.sadellie.indexxo.window.WinUserConst.HTCLIENT
import io.github.sadellie.indexxo.window.WinUserConst.HTMAXBUTTON
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
// https://github.com/Konyaco/compose-fluent-ui/pull/57
fun FrameWindowScope.WindowsWindowFrame(
  icon: Painter?,
  isDarkTheme: Boolean,
  onCloseRequest: () -> Unit,
  onSettingsClick: () -> Unit,
  title: String,
  state: WindowState,
  content: @Composable FrameWindowScope.() -> Unit,
) {
  LaunchedEffect(window) {
    window.findSkiaLayer()?.transparency = true
  }
  WindowStyle(
    isDarkTheme = isDarkTheme,
    backdropType = when {
      isWindows11OrLater() -> WindowBackdrop.Mica
      else -> WindowBackdrop.Solid(MaterialTheme.colorScheme.background)
    }
  )

  val paddingInset = remember { MutableWindowInsets() }
  val maxButtonRect = remember { mutableStateOf(Rect.Zero) }
  val captionBarRect = remember { mutableStateOf(Rect.Zero) }
  val layoutHitTestOwner = rememberLayoutHitTestOwner()

  val procedure = remember(window) {
    ComposeWindowProcedure(
      window = window,
      hitTest = { x, y ->
        when {
          maxButtonRect.value.contains(x, y) -> HTMAXBUTTON
          captionBarRect.value.contains(x, y) && !layoutHitTestOwner.hitTest(x, y) -> HTCAPTION
          else -> HTCLIENT
        }
      },
      onWindowInsetUpdate = { paddingInset.insets = it },
    )
  }

  Column(modifier = Modifier.windowInsetsPadding(paddingInset)) {
    WindowsWindowFrameToolbar(
      modifier = Modifier.onGloballyPositioned { captionBarRect.value = it.boundsInWindow() },
      icon = icon,
      title = title,
      isMaximized = state.placement == WindowPlacement.Maximized,
      onClose = onCloseRequest,
      onRestore = { User32.INSTANCE.ShowWindow(procedure.windowHandle, WinUser.SW_RESTORE) },
      onMaximize = { User32.INSTANCE.ShowWindow(procedure.windowHandle, WinUser.SW_MAXIMIZE) },
      onMinimize = { User32.INSTANCE.CloseWindow(procedure.windowHandle) },
      onMaximizeButtonRectUpdate = { maxButtonRect.value = it },
      onSettingsClick = onSettingsClick,
    )
    HorizontalDivider()
    content()
  }
}

@Composable
fun DialogWindowScope.WindowsDialogWindowFrame(
  icon: Painter?,
  onCloseRequest: () -> Unit,
  title: String,
  content: @Composable DialogWindowScope.() -> Unit,
) {
  Column(
    modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    WindowDraggableArea {
      WindowsDialogWindowFrameToolbar(
        modifier = Modifier,
        icon = icon,
        title = title,
        onClose = onCloseRequest,
      )
    }
    HorizontalDivider()
    content()
  }
}

@Composable
private fun WindowsDialogWindowFrameToolbar(
  modifier: Modifier,
  icon: Painter?,
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
      Row(
        modifier = Modifier.weight(1f).padding(start = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        if (icon != null) {
          AppIcon(icon)
        }
        AppTitle(title)
      }

      CompositionLocalProvider(LocalIndication provides CloseControlButtonIndication) {
        WindowControlWindowsButton(
          painter = painterResource("icons/controls/close.svg"),
          onClick = onClose,
        )
      }
    }
  }
}

@Composable
private fun WindowsWindowFrameToolbar(
  modifier: Modifier,
  icon: Painter?,
  title: String,
  onMaximizeButtonRectUpdate: (Rect) -> Unit,
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
    horizontalArrangement = Arrangement.End
  ) {
    val toolBarContentColor = toolbarContentColor()
    CompositionLocalProvider(LocalContentColor provides toolBarContentColor) {
      Row(
        modifier = Modifier.weight(1f).padding(start = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (icon != null) {
          AppIcon(icon)
        }
        AppTitle(title)
      }

      IconButton(
        onClick = onSettingsClick,
        modifier = Modifier.padding(AdditionalButtonPadding),
      ) {
        Icon(
          imageVector = SymbolsRounded.Settings,
          contentDescription = null,
          modifier = Modifier.padding(AdditionalButtonPadding),
        )
      }

      Spacer(Modifier.width(8.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
      ) {
        WindowControlWindowsButton(
          painter = painterResource("icons/controls/minimize.svg"),
          onClick = onMinimize,
        )

        val positionReporterModifier =
          Modifier.onGloballyPositioned { onMaximizeButtonRectUpdate(it.boundsInWindow()) }
        if (isMaximized) {
          WindowControlWindowsButton(
            modifier = positionReporterModifier,
            painter = painterResource("icons/controls/restore.svg"),
            onClick = onRestore,
          )
        } else {
          WindowControlWindowsButton(
            modifier = positionReporterModifier,
            painter = painterResource("icons/controls/maximize.svg"),
            onClick = onMaximize,
          )
        }

        CompositionLocalProvider(LocalIndication provides CloseControlButtonIndication) {
          WindowControlWindowsButton(
            painter = painterResource("icons/controls/close.svg"),
            onClick = onClose,
          )
        }
      }
    }
  }
}

@Composable
private fun AppIcon(icon: Painter) {
  Image(
    painter = icon,
    contentDescription = null,
    modifier = Modifier.padding(AppIconPadding),
  )
}

@Composable
private fun AppTitle(title: String) {
  Text(
    modifier = Modifier,
    text = title,
    style = MaterialTheme.typography.bodyMedium,
    maxLines = 1,
  )
}

@Composable
private fun WindowControlWindowsButton(
  modifier: Modifier = Modifier,
  painter: Painter,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .clickable { onClick() }
      .requiredWidth(ControlButtonWidth)
      .fillMaxHeight(),
    contentAlignment = Alignment.Center
  ) {
    Icon(painter, null)
  }
}

private fun Rect.contains(x: Float, y: Float): Boolean {
  return x >= left && x < right && y >= top && y < bottom
}

private val ControlBarHeight = 38.dp
private val ControlButtonWidth = 48.dp
private val AppIconPadding = PaddingValues(0.dp, 6.dp)
private val AdditionalButtonPadding = PaddingValues(0.dp, 4.dp)

@Composable
@Preview
private fun PreviewWindowControls() = Previewer {
  Column {
    WindowsWindowFrameToolbar(
      icon = painterResource("icons/logo_light_color.svg"),
      title = stringResource(Res.string.app_name),
      onMinimize = {},
      onRestore = {},
      onMaximize = {},
      modifier = Modifier,
      onClose = {},
      isMaximized = false,
      onMaximizeButtonRectUpdate = {},
      onSettingsClick = {},
    )

    HorizontalDivider()

    WindowsWindowFrameToolbar(
      icon = painterResource("icons/logo_light_color.svg"),
      title = stringResource(Res.string.app_name),
      onMinimize = {},
      onRestore = {},
      onMaximize = {},
      modifier = Modifier,
      onClose = {},
      isMaximized = true,
      onMaximizeButtonRectUpdate = {},
      onSettingsClick = {},
    )

    HorizontalDivider()

    WindowsDialogWindowFrameToolbar(
      modifier = Modifier,
      icon = painterResource("icons/logo_light_color.svg"),
      title = stringResource(Res.string.app_name),
      onClose = {},
    )
  }
}
