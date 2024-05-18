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

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import coil3.ImageLoader
import io.github.sadellie.indexxo.core.designsystem.LocalFileKitPlatformSettings
import io.github.sadellie.indexxo.core.designsystem.LocalImageLoader
import io.github.sadellie.indexxo.core.designsystem.LocalWindowSizeClass
import io.github.sadellie.indexxo.feature.settings.SettingsScreen
import io.github.vinceglb.filekit.core.FileKitPlatformSettings

@Composable
fun MainApp(
  windowSize: DpSize,
  imageLoader: ImageLoader,
  fileKitPlatformSettings: FileKitPlatformSettings?,
) {
  CompositionLocalProvider(
    LocalWindowSizeClass provides remember(key1 = windowSize) { calculateWindowSizeClass(size = windowSize) },
    LocalImageLoader provides imageLoader,
    LocalFileKitPlatformSettings provides fileKitPlatformSettings,
    LocalScrollbarStyle provides ScrollbarStyle(
      minimalHeight = 16.dp,
      thickness = 8.dp,
      shape = RoundedCornerShape(4.dp),
      hoverDurationMillis = 300,
      unhoverColor = MaterialTheme.colorScheme.inverseOnSurface,
      hoverColor = MaterialTheme.colorScheme.inverseSurface,
    ),
  ) {
    Navigator(SettingsScreen) { navigator ->
      FadeTransition(
        navigator = navigator,
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
      )
    }
  }
}
