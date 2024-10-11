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

package io.github.sadellie.indexxo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.DialogWindowScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberDialogState
import androidx.compose.ui.window.rememberWindowState
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.logo_dark_color
import indexxo.composeapp.generated.resources.logo_light_color
import io.github.sadellie.indexxo.core.designsystem.theme.isDarkThemeEnabled
import io.github.sadellie.indexxo.window.LinuxDialogWindowFrame
import io.github.sadellie.indexxo.window.LinuxWindowFrame
import io.github.sadellie.indexxo.window.WindowsDialogWindowFrame
import io.github.sadellie.indexxo.window.WindowsWindowFrame
import org.jetbrains.compose.resources.painterResource
import java.awt.Dimension
import java.awt.Window
import javax.swing.JRootPane

@Composable
fun IndexxoWindow(
  onCloseRequest: () -> Unit,
  state: WindowState = rememberWindowState(),
  visible: Boolean = true,
  title: String = "Untitled",
  transparent: Boolean = false,
  resizable: Boolean = true,
  enabled: Boolean = true,
  focusable: Boolean = true,
  alwaysOnTop: Boolean = false,
  onPreviewKeyEvent: (KeyEvent) -> Boolean = { false },
  onKeyEvent: (KeyEvent) -> Boolean = { false },
  onSettingsClick: () -> Unit,
  content: @Composable FrameWindowScope.() -> Unit,
) {
  val isDarkTheme = isDarkThemeEnabled()
  val icon = if (isDarkTheme) {
    painterResource(Res.drawable.logo_dark_color)
  } else {
    painterResource(Res.drawable.logo_light_color)
  }

  when (DesktopPlatform.Current) {
    DesktopPlatform.Windows -> {
      val isMaximized =
        remember(resizable, state.placement) { state.placement != WindowPlacement.Floating }

      Window(
        onCloseRequest = onCloseRequest,
        state = state,
        visible = visible,
        title = title,
        icon = icon,
        undecorated = false,
        transparent = transparent,
        resizable = resizable and !isMaximized,
        enabled = enabled,
        focusable = focusable,
        alwaysOnTop = alwaysOnTop,
        onPreviewKeyEvent = onPreviewKeyEvent,
        onKeyEvent = onKeyEvent,
        content = {
          LaunchedEffect(window) { setDefaultMinimumWindowSize(window) }

          WindowsWindowFrame(
            icon = icon,
            isDarkTheme = isDarkTheme,
            onCloseRequest = onCloseRequest,
            title = title,
            state = state,
            onSettingsClick = onSettingsClick,
            content = content,
          )
        },
      )
    }

    DesktopPlatform.MacOS -> Window(
      onCloseRequest = onCloseRequest,
      state = state,
      visible = visible,
      title = title,
      icon = icon,
      undecorated = false,
      transparent = transparent,
      resizable = resizable,
      enabled = enabled,
      focusable = focusable,
      alwaysOnTop = alwaysOnTop,
      onPreviewKeyEvent = onPreviewKeyEvent,
      onKeyEvent = onKeyEvent,
      content = {
        LaunchedEffect(window) { setDefaultMinimumWindowSize(window) }
        LaunchedEffect(window, isDarkTheme) { setMacOSPaneColor(window.rootPane, isDarkTheme) }

        content()
      },
    )

    DesktopPlatform.Linux ->
      Window(
        onCloseRequest = onCloseRequest,
        state = state,
        visible = visible,
        title = title,
        icon = icon,
        undecorated = true,
        transparent = transparent,
        resizable = resizable,
        enabled = enabled,
        focusable = focusable,
        alwaysOnTop = alwaysOnTop,
        onPreviewKeyEvent = onPreviewKeyEvent,
        onKeyEvent = onKeyEvent,
        content = {
          LaunchedEffect(window) { setDefaultMinimumWindowSize(window) }

          LinuxWindowFrame(
            onCloseRequest = onCloseRequest,
            onSettingsClick = onSettingsClick,
            title = title,
            state = state,
            content = content,
          )
        },
      )
  }
}

@Composable
fun IndexxoDialogWindow(
  onCloseRequest: () -> Unit,
  state: DialogState = rememberDialogState(),
  visible: Boolean = true,
  title: String = "Untitled",
  icon: Painter? = null,
  undecorated: Boolean = false,
  transparent: Boolean = false,
  resizable: Boolean = true,
  enabled: Boolean = true,
  focusable: Boolean = true,
  alwaysOnTop: Boolean = false,
  onPreviewKeyEvent: ((KeyEvent) -> Boolean) = { false },
  onKeyEvent: ((KeyEvent) -> Boolean) = { false },
  content: @Composable DialogWindowScope.() -> Unit
) {
  when (DesktopPlatform.Current) {
    DesktopPlatform.Windows -> {
      DialogWindow(
        onCloseRequest = onCloseRequest,
        state = state,
        visible = visible,
        title = title,
        icon = icon,
        undecorated = true,
        transparent = transparent,
        resizable = resizable,
        enabled = enabled,
        focusable = focusable,
        alwaysOnTop = alwaysOnTop,
        onPreviewKeyEvent = onPreviewKeyEvent,
        onKeyEvent = onKeyEvent,
        content = {
          LaunchedEffect(window) { setDefaultMinimumWindowSize(window) }

          WindowsDialogWindowFrame(
            icon = icon,
            onCloseRequest = onCloseRequest,
            title = title,
            content = content,
          )
        },
      )
    }

    DesktopPlatform.MacOS -> {
      DialogWindow(
        onCloseRequest = onCloseRequest,
        state = state,
        visible = visible,
        title = title,
        icon = icon,
        undecorated = undecorated,
        transparent = transparent,
        resizable = resizable,
        enabled = enabled,
        focusable = focusable,
        alwaysOnTop = alwaysOnTop,
        onPreviewKeyEvent = onPreviewKeyEvent,
        onKeyEvent = onKeyEvent,
        content = {
          LaunchedEffect(window) { setDefaultMinimumWindowSize(window) }
          val isDarkTheme = isDarkThemeEnabled()
          LaunchedEffect(window, isDarkTheme) { setMacOSPaneColor(window.rootPane, isDarkTheme) }

          content()
        },
      )
    }

    DesktopPlatform.Linux -> {
      DialogWindow(
        onCloseRequest = onCloseRequest,
        state = state,
        visible = visible,
        title = title,
        icon = icon,
        undecorated = true,
        transparent = transparent,
        resizable = resizable,
        enabled = enabled,
        focusable = focusable,
        alwaysOnTop = alwaysOnTop,
        onPreviewKeyEvent = onPreviewKeyEvent,
        onKeyEvent = onKeyEvent,
        content = {
          LaunchedEffect(window) { setDefaultMinimumWindowSize(window) }

          LinuxDialogWindowFrame(
            onCloseRequest = onCloseRequest,
            title = title,
            content = content,
          )
        },
      )
    }
  }
}

private fun setDefaultMinimumWindowSize(window: Window) {
  window.minimumSize = Dimension(200, 200)
}

private fun setMacOSPaneColor(window: JRootPane, isDarkTheme: Boolean) {
  window.putClientProperty(
    "apple.awt.windowAppearance",
    if (isDarkTheme) "NSAppearanceNameVibrantDark" else "NSAppearanceNameVibrantLight",
  )
}
