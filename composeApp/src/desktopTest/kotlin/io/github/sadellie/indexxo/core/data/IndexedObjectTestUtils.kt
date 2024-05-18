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

import io.github.sadellie.indexxo.core.common.attributes
import io.github.sadellie.indexxo.core.common.localDateTimeNow
import io.github.sadellie.indexxo.core.common.toLocalDateTime
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObject
import io.github.sadellie.indexxo.core.model.IndexedObjectImpl
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toOkioPath
import java.io.File
import java.nio.file.attribute.BasicFileAttributes

fun String.toIndexedObject() = runBlocking {
  processFile(File(this@toIndexedObject), emptyList())
}

fun File.toIndexedObjectImplDocument(
  fake: Boolean = false
): IndexedObject {
  var attributes: BasicFileAttributes? = null
  if (!fake) {
    attributes = attributes()
  }
  return IndexedObjectImpl(
    path = this.toOkioPath(),
    parentPath = this.toOkioPath().parent,
    sizeBytes = this.length(),
    fileCategory = FileCategory.DOCUMENT,
    createdDate = attributes?.creationTime()?.toLocalDateTime() ?: localDateTimeNow(),
    modifiedDate = attributes?.lastModifiedTime()?.toLocalDateTime() ?: localDateTimeNow(),
  )
}

fun File.toIndexedObjectImplFolder(
  fake: Boolean = false
): IndexedObject {
  var attributes: BasicFileAttributes? = null
  if (!fake) {
    attributes = attributes()
  }

  return IndexedObjectImpl(
    path = this.toOkioPath(),
    parentPath = this.toOkioPath().parent,
    sizeBytes = 0,
    fileCategory = FileCategory.FOLDER,
    createdDate = attributes?.creationTime()?.toLocalDateTime() ?: localDateTimeNow(),
    modifiedDate = attributes?.lastModifiedTime()?.toLocalDateTime() ?: localDateTimeNow(),
  )
}
