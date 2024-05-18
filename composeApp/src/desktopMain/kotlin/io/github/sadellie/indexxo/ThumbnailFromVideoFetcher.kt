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

import co.touchlab.kermit.Logger
import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import io.github.sadellie.indexxo.core.model.ThumbnailForVideo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Buffer
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Java2DFrameConverter
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.math.roundToInt

class ThumbnailFromVideoFetcher(
  private val thumbnailForVideo: ThumbnailForVideo,
  private val options: Options,
) : Fetcher {
  override suspend fun fetch(): FetchResult? =
    withContext(Dispatchers.IO) {
      return@withContext try {
        Logger.v { "Fetch: ${thumbnailForVideo.indexedObject.path}" }
        val converter = Java2DFrameConverter()
        val grabber = FFmpegFrameGrabber(thumbnailForVideo.indexedObject.path.toFile())
        grabber.start()
        grabber.setVideoFrameNumber(
          (grabber.lengthInFrames * thumbnailForVideo.framePosition)
            .roundToInt()
            .coerceIn(0, grabber.lengthInFrames)
        )
        val frame = grabber.grabImage()
        val bufferedImage = converter.getBufferedImage(frame)
        grabber.stop()
        val outputStream =
          ByteArrayOutputStream().apply { ImageIO.write(bufferedImage, "jpg", this) }

        val bufferedSource = Buffer().apply { write(outputStream.toByteArray()) }

        val sourceFetchResult =
          SourceFetchResult(
            source = ImageSource(source = bufferedSource, fileSystem = options.fileSystem),
            mimeType = null,
            dataSource = DataSource.MEMORY,
          )

        sourceFetchResult
      } catch (e: Exception) {
        Logger.e(e) { "Failed to fetch" }
        null
      }
    }

  class Factory : Fetcher.Factory<ThumbnailForVideo> {
    override fun create(
      data: ThumbnailForVideo,
      options: Options,
      imageLoader: ImageLoader,
    ): Fetcher {
      return ThumbnailFromVideoFetcher(data, options)
    }
  }
}
