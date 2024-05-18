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

import co.touchlab.kermit.DefaultFormatter
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Message
import co.touchlab.kermit.MessageStringFormatter
import co.touchlab.kermit.Severity
import co.touchlab.kermit.Tag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException

class FileLogWriter(private val messageStringFormatter: MessageStringFormatter = DefaultFormatter) :
  LogWriter() {
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
  private val queue = Channel<Job>(Channel.UNLIMITED)
  private val writer by lazy {
    val logFolder = (dataDirectory / "logs").toFile()
    if (!logFolder.exists()) logFolder.mkdirs()
    logFolder.resolve("${System.currentTimeMillis()}.log").bufferedWriter()
  }

  init {
    scope.launch { for (job in queue) job.join() }
  }

  override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
    if (!isLoggable(tag, severity)) return

    val formattedMessage =
      messageStringFormatter.formatMessage(severity, Tag(tag), Message(message))
    appendNewLine(formattedMessage)

    val stackTrace = throwable?.stackTraceToString()
    if (stackTrace != null) {
      appendNewLine(stackTrace)
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  fun close() {
    queue.trySend(scope.launch(start = CoroutineStart.LAZY) { writer.close() })
    runBlocking {
      // Check every 500ms if FileLogWriter has finished tasks
      while (!queue.isEmpty or !isWriterClosed()) {
        // Writer closes almost immediately
        delay(CLOSE_WRITER_LOOP_MILLIS)
      }
      // All jobs are processed, writer is closed
    }
  }

  private fun appendNewLine(text: String) {
    queue.trySend(scope.launch(start = CoroutineStart.LAZY) { writer.appendLine(text) })
  }

  private fun isWriterClosed() =
    try {
      writer.flush()
      false
    } catch (e: IOException) {
      println(e)
      println("FileLogWriter is closed")
      true
    }

}

private const val CLOSE_WRITER_LOOP_MILLIS = 500L
