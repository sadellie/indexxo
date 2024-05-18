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

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import co.touchlab.kermit.Logger
import coil3.compose.AsyncImage
import coil3.toUri
import io.github.sadellie.indexxo.core.designsystem.LocalImageLoader
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.BrokenImage
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.FileOpen
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Folder
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Refresh
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObject
import io.github.sadellie.indexxo.core.model.ThumbnailForVideo

@Composable
fun FileThumbnail(
  modifier: Modifier,
  item: IndexedObject,
  contentScale: ContentScale = ContentScale.Crop,
) {
  when (item.fileCategory) {
    FileCategory.FOLDER -> Icon(
      imageVector = SymbolsRounded.Folder,
      contentDescription = null,
      modifier = modifier.scale(ICON_THUMBNAIL_SCALE),
    )

    FileCategory.IMAGE -> BasicImage(
      modifier = modifier,
      contentScale = contentScale,
      data = remember(item.path) { item.path.toString().toUri() },
    )

    FileCategory.VIDEO -> BasicImage(
      modifier = modifier,
      contentScale = contentScale,
      data = ThumbnailForVideo(item, VIDEO_THUMBNAIL_FRAME_POSITION),
    )

    else -> Icon(
      imageVector = SymbolsRounded.FileOpen,
      contentDescription = null,
      modifier = modifier.scale(ICON_THUMBNAIL_SCALE),
    )
  }
}

@Composable
private fun BasicImage(
  modifier: Modifier,
  contentScale: ContentScale,
  data: Any
) {
  val tint = LocalContentColor.current
  val contentColorFilter = remember(tint) { ColorFilter.tint(tint) }

  AsyncImage(
    model = data,
    imageLoader = LocalImageLoader.current,
    placeholder = rememberVectorPainterWithColorFilter(SymbolsRounded.Refresh, contentColorFilter),
    error = rememberVectorPainterWithColorFilter(SymbolsRounded.BrokenImage, contentColorFilter),
    onError = { Logger.e(TAG, it.result.throwable) { "Failed to load: $it" } },
    contentDescription = null,
    modifier = modifier,
    contentScale = contentScale,
  )
}

/**
 * Wraps [rememberVectorPainter] with [colorFilter] overwritten.
 *
 * https://gist.github.com/colinrtwhite/c2966e0b8584b4cdf0a5b05786b20ae1
 */
@Composable
private fun rememberVectorPainterWithColorFilter(
  imageVector: ImageVector,
  colorFilter: ColorFilter? = null,
): Painter = ForwardingPainter(rememberVectorPainter(imageVector), colorFilter)

private class ForwardingPainter(
  private val painter: Painter,
  private var colorFilter: ColorFilter?,
) : Painter() {
  override val intrinsicSize get() = painter.intrinsicSize

  override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
    if (colorFilter != null) {
      this.colorFilter = colorFilter
    }
    return true
  }

  override fun DrawScope.onDraw() = with(painter) {
    draw(size, DefaultAlpha, this@ForwardingPainter.colorFilter)
  }
}

private const val ICON_THUMBNAIL_SCALE = 0.8f
private const val VIDEO_THUMBNAIL_FRAME_POSITION = 0.5f
private const val TAG = "FileThumbnail"
