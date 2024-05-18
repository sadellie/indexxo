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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import coil3.SingletonImageLoader
import coil3.compose.LocalPlatformContext
import io.github.sadellie.indexxo.core.common.navigateToDownloadPage
import io.github.sadellie.indexxo.core.designsystem.LocalWindowSizeClass
import io.github.sadellie.indexxo.core.designsystem.theme.IndexxoTheme
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
  ComposeViewport(document.body!!) {
    IndexxoTheme {
      Column {
        val containerSize = LocalWindowInfo.current.containerSize
        val windowsSize = with(LocalDensity.current) {
          DpSize(containerSize.width.toDp(), containerSize.height.toDp())
        }
        DemoTopBar()
        HorizontalDivider()
        MainApp(
          windowSize = windowsSize,
          imageLoader = SingletonImageLoader.get(LocalPlatformContext.current),
          fileKitPlatformSettings = null,
        )
      }
    }
  }
}

@Composable
private fun DemoTopBar() {
  Row(
    modifier = Modifier
      .background(MaterialTheme.colorScheme.background)
      .fillMaxWidth()
      .padding(8.dp),
    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    if (LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact) {
      Text(
        text = "Just a demo",
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.weight(1f),
        textAlign = TextAlign.Center,
      )
    } else {
      Text(
        text = "This is just a demo, try the real thing for free",
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
      )
    }

    Button(
      onClick = ::navigateToDownloadPage,
    ) {
      Text("Download now")
    }
  }
}
