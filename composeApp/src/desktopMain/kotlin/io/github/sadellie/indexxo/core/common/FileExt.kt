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

package io.github.sadellie.indexxo.core.common

import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import okio.Path.Companion.toPath
import java.awt.Desktop
import java.awt.HeadlessException
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime
import java.util.zip.CRC32C

val resourcesDir =
  (System.getProperty("compose.application.resources.dir") ?: "include/common").toPath()

suspend fun File.calculatePartialHash(sampleSizeBytes: Int): Long =
  withContext(Dispatchers.IO) {
    val file = this@calculatePartialHash
    val crc32 = CRC32C()
    crc32.update(file.readFirst(sampleSizeBytes))
    return@withContext crc32.value
  }

suspend fun File.calculateFullHash(bufferSize: Int): Long = withContext(Dispatchers.IO) {
  val file = this@calculateFullHash
  file.inputStream().buffered(bufferSize).use { reader ->
    val buffer = ByteArray(bufferSize)
    val crc32 = CRC32C()
    var bytesLeft = reader.read(buffer)

    while (bytesLeft >= 0) {
      crc32.update(buffer, 0, bytesLeft)
      bytesLeft = reader.read(buffer)
    }

    return@withContext crc32.value
  }
}

fun File.readFirst(bufferSize: Int): ByteArray {
  val buffer = ByteArray(bufferSize)
  this.inputStream().buffered(bufferSize).use { it.read(buffer) }

  return buffer
}

fun File.openInSystem() = try {
  Desktop.getDesktop().open(this)
} catch (e: HeadlessException) {
  Logger.w(e) { "System is in headless mode" }
} catch (e: UnsupportedOperationException) {
  Logger.w(e) { "Can't open file. Operation is not supported by system" }
} catch (e: IllegalArgumentException) {
  Logger.w(e) { "${this.path} doesn't exist" }
} catch (e: IOException) {
  Logger.w(e) { "No application to open ${this.path}" }
} catch (e: SecurityException) {
  Logger.w(e) { "Security error while opening ${this.path}" }
}

fun File.moveToTrash() = try {
  Desktop.getDesktop().moveToTrash(this)
} catch (e: HeadlessException) {
  Logger.w(e) { "System is in headless mode" }
} catch (e: UnsupportedOperationException) {
  Logger.w(e) { "Can't move to trash. Operation is not supported by system" }
} catch (e: IllegalArgumentException) {
  Logger.w(e) { "${this.path} doesn't exist" }
} catch (e: SecurityException) {
  Logger.w(e) { "Security error while deleting ${this.path}" }
}

fun File.attributes(): BasicFileAttributes =
  Files.readAttributes(toPath(), BasicFileAttributes::class.java)

const val MAX_BUFFER_SIZE = 8 * 1_024
const val SAMPLE_SIZE = MAX_BUFFER_SIZE

fun FileTime.toLocalDateTime(): LocalDateTime =
  Instant.fromEpochMilliseconds(this.toMillis()).toLocalDateTime(TimeZone.currentSystemDefault())
