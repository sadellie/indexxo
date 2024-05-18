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

package io.github.sadellie.indexxo.core.model

import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.archive
import indexxo.composeapp.generated.resources.audio
import indexxo.composeapp.generated.resources.document
import indexxo.composeapp.generated.resources.other
import indexxo.composeapp.generated.resources.folder
import indexxo.composeapp.generated.resources.image
import indexxo.composeapp.generated.resources.video
import org.jetbrains.compose.resources.StringResource

enum class FileCategory(val res: StringResource) {
  IMAGE(res = Res.string.image),
  VIDEO(res = Res.string.video),
  AUDIO(res = Res.string.audio),
  DOCUMENT(res = Res.string.document),
  FOLDER(res = Res.string.folder),
  ARCHIVE(res = Res.string.archive),
  OTHER(res = Res.string.other);

  companion object {
    // https://github.com/dyne/file-extension-list
    val image: Set<String> by lazy {
      setOf(
        "bmp",
        "dib",
        "jpeg",
        "jpg",
        "jpe",
        "jp2",
        "png",
        "webp",
        "avif",
        "pbm",
        "pgm",
        "ppm",
        "pxm",
        "pnm",
        "pfm",
        "sr",
        "ras",
        "tiff",
        "tif",
        "exr",
        "hdr",
        "pic",
        "3dm",
        "3ds",
        "max",
        "dds",
        "gif",
        "psd",
        "xcf",
        "tga",
        "thm",
        "yuv",
        "ai",
        "eps",
        "ps",
        "svg",
        "dwg",
        "dxf",
        "gpx",
        "kml",
        "kmz",
      )
    }
    val video: Set<String> by lazy {
      setOf(
        "3g2",
        "3gp",
        "aaf",
        "asf",
        "avchd",
        "avi",
        "drc",
        "flv",
        "m2v",
        "m4p",
        "m4v",
        "mkv",
        "mng",
        "mov",
        "mp2",
        "mp4",
        "mpe",
        "mpeg",
        "mpg",
        "mpv",
        "mxf",
        "nsv",
        "ogg",
        "ogv",
        "ogm",
        "qt",
        "rm",
        "rmvb",
        "roq",
        "srt",
        "svi",
        "vob",
        "webm",
        "wmv",
        "yuv",
      )
    }
    val document: Set<String> by lazy {
      setOf(
        "doc",
        "docx",
        "ebook",
        "log",
        "md",
        "msg",
        "odt",
        "org",
        "pages",
        "pdf",
        "rtf",
        "rst",
        "tex",
        "txt",
        "wpd",
        "wps",
      )
    }
    val audio: Set<String> by lazy {
      setOf(
        "aac",
        "aiff",
        "ape",
        "au",
        "flac",
        "gsm",
        "it",
        "m3u",
        "m4a",
        "mid",
        "mod",
        "mp3",
        "mpa",
        "pls",
        "ra",
        "s3m",
        "sid",
        "wav",
        "wma",
        "xm",
      )
    }
    val archive: Set<String> by lazy {
      setOf(
        "7z",
        "a",
        "apk",
        "ar",
        "bz2",
        "cab",
        "cpio",
        "deb",
        "dmg",
        "egg",
        "gz",
        "iso",
        "jar",
        "lha",
        "mar",
        "pea",
        "rar",
        "rpm",
        "s7z",
        "shar",
        "tar",
        "tbz2",
        "tgz",
        "tlz",
        "war",
        "whl",
        "xpi",
        "zip",
        "zipx",
        "xz",
        "pak",
      )
    }
  }
}
