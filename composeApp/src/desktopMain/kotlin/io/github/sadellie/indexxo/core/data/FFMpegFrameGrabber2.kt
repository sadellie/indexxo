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

package io.github.sadellie.indexxo.core.data

import co.touchlab.kermit.Logger
import org.bytedeco.ffmpeg.avcodec.AVCodec
import org.bytedeco.ffmpeg.avcodec.AVPacket
import org.bytedeco.ffmpeg.avformat.AVFormatContext
import org.bytedeco.ffmpeg.global.avcodec
import org.bytedeco.ffmpeg.global.avformat
import org.bytedeco.ffmpeg.global.avutil
import org.bytedeco.ffmpeg.global.swscale
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.DoublePointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.javacv.Frame
import java.io.File
import java.nio.Buffer

class FFMpegFrameGrabber2(file: File, threads: Int) : AutoCloseable {
  data object EndOfFile : Exception("End of file") {
    private fun readResolve(): Any = EndOfFile
  }

  private val formatContext: AVFormatContext =
    AVFormatContext(null).also {
      avformat
        .avformat_open_input(it, file.path, null, null)
        .checkNativeResult { "Failed to open video file" }
      avformat
        .avformat_find_stream_info(it, null as PointerPointer<*>?)
        .checkNativeResult { "Failed to find video stream info" }
    }
  private val codec = avcodec.avcodec_find_decoder_by_name("h264") ?: AVCodec()
    .also { Logger.w(TAG) { "Codec h264_cuvid not found, fall back to default ${it.name().string}" } }
  private val streamIndex =
    avformat.av_find_best_stream(formatContext, avutil.AVMEDIA_TYPE_VIDEO, -1, -1, codec, 0)

  // Do not trust! ffmpeg guesses this value
  private val frameRate: Double =
    with(formatContext.streams(streamIndex).r_frame_rate()) { num().toDouble() / den() }
  private val lengthInTime = formatContext.duration()
  private val lengthInFrames = lengthInTime * frameRate / avutil.AV_TIME_BASE
  private val codecContext =
    avcodec.avcodec_alloc_context3(codec).also { ctx ->
      ctx.thread_count(threads)
      avcodec
        .avcodec_parameters_to_context(ctx, formatContext.streams(streamIndex).codecpar())
        .checkNativeResult { "Failed to load codec parameters" }
      avcodec
        .avcodec_open2(ctx, codec, null as PointerPointer<*>?)
        .checkNativeResult { "Failed to open codec: $it" }
    }
  private val frame = avutil.av_frame_alloc()
  private val frameRGB = avutil.av_frame_alloc()

  // Gray!, Use BGR (not RGB) to get colors
  private val pixFormat = avutil.AV_PIX_FMT_YUV420P
  private val numBytes =
    avutil.av_image_get_buffer_size(pixFormat, codecContext.width(), codecContext.height(), 1)
  private val imagePtr =
    arrayOf(BytePointer(avutil.av_malloc(numBytes.toLong())).capacity(numBytes.toLong()))
  private val imageBuf = arrayOf<Buffer>(imagePtr[0].asBuffer())
  private val swsContext =
    swscale
      .sws_getContext(
        codecContext.width(), codecContext.height(), codecContext.pix_fmt(),
        codecContext.width(), codecContext.height(), pixFormat,
        swscale.SWS_BILINEAR, null, null, null as DoublePointer?,
      )
      .also {
        avutil.av_image_fill_arrays(
          frameRGB.data(),
          frameRGB.linesize(),
          imagePtr[0],
          pixFormat,
          codecContext.width(),
          codecContext.height(),
          1,
        )
        frameRGB.format(pixFormat)
        frameRGB.width(codecContext.width())
        frameRGB.height(codecContext.height())
      }
  private val packet: AVPacket = AVPacket()

  fun processFrames(
    preferredFPS: Double,
    block: (frame: Frame) -> Unit
  ) {
    var currentFrame = 0
    val framesToSkip = frameRate / preferredFPS.coerceAtMost(frameRate)
    while (currentFrame <= lengthInFrames) {
      try {
        moveCursorAndReadFrame()
      } catch (e: EndOfFile) {
        break
      }

      val shouldBeProcessed = currentFrame % framesToSkip == 0.0
      if (shouldBeProcessed) {
        block(grabFrame())
      }
      currentFrame++
    }
  }

  override fun close() {
    avcodec.av_packet_unref(packet)
    avutil.av_frame_free(frame)
    avutil.av_frame_free(frameRGB)
    avcodec.avcodec_close(codecContext)
    avformat.avformat_close_input(formatContext)
    swscale.sws_freeContext(swsContext)
  }

  private fun moveCursorAndReadFrame() {
    var readFrameResult: Int

    while (true) {
      readFrameResult = avformat.av_read_frame(formatContext, packet)

      when (readFrameResult) {
        0 -> {
          if (packet.stream_index() == streamIndex) {
            avcodec.avcodec_send_packet(codecContext, packet)
              .checkNativeResult { "Failed to send packet" }

            when (avcodec.avcodec_receive_frame(codecContext, frame)) {
              0 -> break
              else -> {}
            }
          }
        }
        avutil.AVERROR_EOF -> throw EndOfFile
        else -> error("Uncaught error when reading frame: $readFrameResult")
      }
    }
  }

  private fun grabFrame(): Frame {
    swscale.sws_scale(
      swsContext,
      frame.data(),
      frame.linesize(),
      0,
      codecContext.height(),
      frameRGB.data(),
      frameRGB.linesize(),
    )

    val processedFrame = Frame()
    processedFrame.imageWidth = codecContext.width()
    processedFrame.imageHeight = codecContext.height()
    processedFrame.imageDepth = Frame.DEPTH_UBYTE
    processedFrame.imageStride = frameRGB.linesize(0)
    processedFrame.image = imageBuf
    processedFrame.opaque = frameRGB
    processedFrame.timestamp = frame.best_effort_timestamp()
    processedFrame.image[0].limit(processedFrame.imageHeight * processedFrame.imageStride)
    processedFrame.imageChannels = processedFrame.imageStride / processedFrame.imageWidth

    return processedFrame
  }
}

private fun Int.checkNativeResult(
  message: (Int) -> String
) = also { if (it < 0) throw Exception(message(it)) }

private const val TAG = "FFMpegFrameGrabber2"
