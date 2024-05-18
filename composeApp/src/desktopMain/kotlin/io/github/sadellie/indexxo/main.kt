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

import androidx.compose.foundation.LocalContextMenuRepresentation
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import co.touchlab.kermit.Logger
import coil3.ImageLoader
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.app_name
import indexxo.composeapp.generated.resources.loading
import io.github.sadellie.indexxo.core.data.AppConfig
import io.github.sadellie.indexxo.core.data.PreferencesRepository
import io.github.sadellie.indexxo.core.designsystem.LocalWindowFocusRequester
import io.github.sadellie.indexxo.core.designsystem.WindowFocusRequester
import io.github.sadellie.indexxo.core.designsystem.theme.IndexxoContextMenuRepresentation
import io.github.sadellie.indexxo.core.designsystem.theme.IndexxoTheme
import io.github.sadellie.indexxo.core.model.ThemingMode
import io.github.vinceglb.filekit.core.FileKitPlatformSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.startKoin
import org.koin.core.lazyModules
import org.koin.core.waitAllStartJobs
import org.koin.java.KoinJavaComponent.get
import org.koin.mp.KoinPlatform
import kotlin.system.exitProcess

fun main(args: Array<String>) {
  Logger.d(TAG) { "App start" }
  AppConfig.loadFromArgs(args)
  Logger.d(TAG) { "buildType: ${AppConfig.buildType}"}
  Logger.d(TAG) { "realDelete: ${AppConfig.realDelete}"}
  Logger.d(TAG) { "logSeverity: ${AppConfig.logSeverity}"}
  Logger.d(TAG) { "logToFile: ${AppConfig.logToFile}"}

  try {
    application(exitProcessOnExit = false) {
      var showSplashScreen by remember { mutableStateOf(true) }

      LaunchedEffect(Unit) {
        setupLogger()
        setupKoin()
        showSplashScreen = false
      }

      if (showSplashScreen) {
        SplashScreenWindow(onCloseRequest = this::exitApplication)
      } else {
        MainAppWindow(onCloseRequest = this::exitApplication)
      }
    }
  } catch (t: Throwable) {
    Logger.e(t, TAG) { "Uncaught error" }
  }

  if (AppConfig.logToFile) {
    closeFileLogWriters()
  }
  exitProcess(0)
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun MainAppWindow(onCloseRequest: () -> Unit) {
  val windowState = rememberWindowState(
    width = 839.dp, // Medium (840 - 1 to trigger)
    height = 900.dp, // Expanded
  )
  val themingMode = get<PreferencesRepository>(PreferencesRepository::class.java)
    .indexxoPreferencesFlow
    .mapLatest { it.themingMode }
    .collectAsState(null)
    .value

  IndexxoTheme(
    darkTheme = when (themingMode) {
      ThemingMode.AUTO -> isSystemInDarkTheme()
      ThemingMode.FORCE_LIGHT -> false
      ThemingMode.FORCE_DARK -> true
      null -> return
    },
  ) {
    var showSettingsDialog by rememberSaveable { mutableStateOf(false) }

    IndexxoWindow(
      title = stringResource(Res.string.app_name),
      state = windowState,
      onCloseRequest = onCloseRequest,
      onSettingsClick = { showSettingsDialog = true },
    ) {
      CompositionLocalProvider(
        LocalContextMenuRepresentation provides IndexxoContextMenuRepresentation,
        LocalWindowFocusRequester provides WindowFocusRequesterImpl(window),
      ) {
        MainApp(
          windowSize = windowState.size,
          imageLoader = get(ImageLoader::class.java),
          fileKitPlatformSettings = FileKitPlatformSettings(window),
        )
      }

      if (showSettingsDialog) {
        SettingsDialogWindow(
          onCloseRequest = { showSettingsDialog = false },
        )
      }
    }
  }
}

@Composable
private fun SplashScreenWindow(onCloseRequest: () -> Unit) {
  val windowState =
    rememberWindowState(
      position = WindowPosition(Alignment.Center),
      width = Dp.Unspecified,
      height = Dp.Unspecified,
    )
  IndexxoTheme(isSystemInDarkTheme()) {
    Window(
      state = windowState,
      onCloseRequest = onCloseRequest,
      undecorated = true,
      resizable = false,
      alwaysOnTop = true,
      transparent = true,
    ) {
      Column(
        modifier =
        Modifier.clip(RoundedCornerShape(16.dp))
          .background(MaterialTheme.colorScheme.background)
          .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
      ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
          Text(stringResource(Res.string.app_name), style = MaterialTheme.typography.displayLarge)
          Text(stringResource(Res.string.loading))
        }
      }
    }
  }
}

private fun setupLogger() {
  // avutil is used in ffmpeg wrapper
  try {
    // Reflection due to conflicting with dependencies, no errors in runtime
    val avutil = Class.forName("org.bytedeco.ffmpeg.global.avutil")
    avutil
      .getMethod("av_log_get_level")
      .invoke(null) as Int

    avutil
      .getMethod("av_log_set_level", Int::class.java)
      // AV_LOG_ERROR = 16
      .invoke(null, 16)

  } catch (t: Throwable) {
    Logger.w(t) { "Failed to set avutil log level" }
  }

  Logger.d(TAG) { "Set minSeverity to ${AppConfig.logSeverity}" }
  Logger.setMinSeverity(AppConfig.logSeverity)

  if (AppConfig.logToFile) {
    try {
      Logger.d(TAG) { "Initiating FileLogWriter" }
      val fileLogWriter = FileLogWriter()
      Logger.addLogWriter(fileLogWriter)
      Logger.d(TAG) { "Added FileLogWriter" }
    } catch (e: Exception) {
      Logger.w(e, TAG) {
        "Failed to initiate FileLogWriter. Only CommonWriter (println) will be used"
      }
    }
  } else {
    Logger.d(TAG) { "Logging to file is disabled. Only CommonWriter (println) will be used" }
  }
}

@OptIn(KoinExperimentalAPI::class)
private fun setupKoin() {
  startKoin {
    lazyModules(
      userData,
      dispatcher = Dispatchers.IO,
    )
    lazyModules(
      repositories,
      screenModels,
      coilImageLoader,
      dispatcher = Dispatchers.Default,
    )
  }
  KoinPlatform.getKoin().waitAllStartJobs()
}

private fun closeFileLogWriters() {
  Logger.i(TAG) { "Closing application" }
  Logger.config.logWriterList.filterIsInstance<FileLogWriter>().forEach { it.close() }
}

private class WindowFocusRequesterImpl(private val window: ComposeWindow) : WindowFocusRequester {
  override fun requestFocus() = window.requestFocus()
}

private const val TAG = "main"
