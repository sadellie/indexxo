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
import io.github.sadellie.indexxo.core.common.attributes
import io.github.sadellie.indexxo.core.common.extension
import io.github.sadellie.indexxo.core.common.mapAsyncAwaitAll
import io.github.sadellie.indexxo.core.common.toLocalDateTime
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.FileCategory.Companion.archive
import io.github.sadellie.indexxo.core.model.FileCategory.Companion.audio
import io.github.sadellie.indexxo.core.model.FileCategory.Companion.document
import io.github.sadellie.indexxo.core.model.FileCategory.Companion.video
import io.github.sadellie.indexxo.core.model.IndexedObject
import io.github.sadellie.indexxo.core.model.IndexedObjectImpl
import io.github.sadellie.indexxo.core.model.Indexing
import io.github.sadellie.indexxo.core.model.IndexingStage
import io.github.sadellie.indexxo.core.model.Walking
import io.github.sadellie.indexxo.core.model.Warning
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withContext
import okio.Path
import okio.Path.Companion.toOkioPath
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

suspend fun indexWithWarnings(
  includedPaths: List<Path>,
  excludedPaths: List<Path>,
  includedExtensions: List<String>,
  excludedExtensions: List<String>,
  maxThreads: Int,
  callback: (IndexingStage) -> Unit,
): Pair<List<IndexedObject>, List<Warning>> =
  withContext(Dispatchers.IO) {
    val applyIncludedExtensionCheck = includedExtensions.isNotEmpty()
    val loweredExcludedExtensions = excludedExtensions.map { it.lowercase() }
    val loweredIncludedExtensions = includedExtensions.map { it.lowercase() }

    val allFiles: List<File> = includedPaths.map { dir ->
      dir.toFile()
        .walkBottomUp()
        .onEnter { directory ->
          val enteredFilePath = directory.toOkioPath()
          callback(Walking(enteredFilePath))
          // Check for path
          enteredFilePath !in excludedPaths
        }
        .filter { file ->
          val filePath = file.toOkioPath()
          if (filePath in excludedPaths) return@filter false

          // Check for extension
          if (!file.isDirectory()) {
            val loweredExtension  = filePath.extension.lowercase()
            if (loweredExtension in loweredExcludedExtensions) return@filter false
            if (applyIncludedExtensionCheck) {
              if (loweredExtension !in loweredIncludedExtensions) return@filter false
            }
          }
          true
        }
    }
      .flatMap { it }
      .distinctBy { it.path }

    val semaphore = Semaphore(maxThreads)
    val atomicCounter = AtomicInteger()
    val allIndexedObjectsWithWarnings = allFiles.mapAsyncAwaitAll(
      scope = this@withContext,
      semaphore = semaphore,
    ) { file ->
      val progress = atomicCounter.incrementAndGet().toFloat() / allFiles.size

      val indexedObjectResult = try {
        processFile(file, includedPaths)
          .also { callback(Indexing(progress, it)) }
      } catch (e: Exception) {
        Warning(file.toOkioPath(), e.message, e.stackTraceToString())
      }
      indexedObjectResult
    }

    Logger.d(TAG) { "Collecting indexed objects" }
    val indexedObjects = allIndexedObjectsWithWarnings.filterIsInstance<IndexedObject>()
    Logger.d(TAG) { "Collecting warning" }
    val warnings = allIndexedObjectsWithWarnings.filterIsInstance<Warning>()

    return@withContext indexedObjects to warnings
  }

suspend fun processFile(
  file: File,
  include: List<Path>,
): IndexedObject =
  withContext(Dispatchers.IO) {
    if (!file.exists()) error("Doesn't exist: ${file.path}")
    val path = file.toOkioPath()
    val parentPath = if (path in include) null else file.parentFile.toOkioPath()

    val attributes = file.attributes()
    val createdDate = attributes.creationTime().toLocalDateTime()
    val modifiedDate = attributes.lastModifiedTime().toLocalDateTime()

    val category = file.categorize()
    val size = if (category != FileCategory.FOLDER) file.length() else 0

    val indexedObject = IndexedObjectImpl(
      path = path,
      parentPath = parentPath,
      sizeBytes = size,
      fileCategory = category,
      createdDate = createdDate,
      modifiedDate = modifiedDate,
    )

    return@withContext indexedObject
  }

private fun File.categorize(): FileCategory = if (isDirectory) {
  FileCategory.FOLDER
} else {
  when (extension.lowercase()) {
    in FileCategory.image -> FileCategory.IMAGE
    in video -> FileCategory.VIDEO
    in document -> FileCategory.DOCUMENT
    in audio -> FileCategory.AUDIO
    in archive -> FileCategory.ARCHIVE
    else -> FileCategory.OTHER
  }
}

private const val TAG = "Indexer"
