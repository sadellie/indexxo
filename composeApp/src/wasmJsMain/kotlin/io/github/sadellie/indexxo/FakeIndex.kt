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

import io.github.sadellie.indexxo.core.common.localDateTimeNow
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObject
import io.github.sadellie.indexxo.core.model.IndexedObjectImpl
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import okio.Path.Companion.toPath

// There are no rules in demo
val fakeIndex: List<IndexedObject> by lazy {
  val timeZone = TimeZone.of("UTC")
  val folders = List(6) {
    val createdDate = randomDate(timeZone)
    IndexedObjectImpl(
      path = "/folder$it".toPath(),
      parentPath = null,
      sizeBytes = 0,
      fileCategory = FileCategory.FOLDER,
      createdDate = createdDate,
      modifiedDate = createdDate,
    )
  }

  val documents = List(26) {
    val createdDate = randomDate(timeZone)
    IndexedObjectImpl(
      path = "/folder1/document$it.${FileCategory.document.random()}".toPath(),
      parentPath = "/folder1".toPath(),
      sizeBytes = randomSizeBytes(),
      fileCategory = FileCategory.DOCUMENT,
      createdDate = createdDate,
      modifiedDate = createdDate,
    )
  }

  val images = List(17) {
    val createdDate = randomDate(timeZone)
    IndexedObjectImpl(
      path = "/folder1/image$it.${FileCategory.image.random()}".toPath(),
      parentPath = "/folder1".toPath(),
      sizeBytes = randomSizeBytes(),
      fileCategory = FileCategory.IMAGE,
      createdDate = createdDate,
      modifiedDate = createdDate,
    )
  }

  val videos = List(12) {
    val createdDate = randomDate(timeZone)
    IndexedObjectImpl(
      path = "/folder1/video$it.${FileCategory.video.random()}".toPath(),
      parentPath = "/folder1".toPath(),
      sizeBytes = randomSizeBytes(),
      fileCategory = FileCategory.VIDEO,
      createdDate = createdDate,
      modifiedDate = createdDate,
    )
  }

  (folders + images + videos + documents).shuffled()
}

private fun randomDate(timeZone: TimeZone) = localDateTimeNow()
  .toInstant(timeZone)
  .minus((1..86_400).random(), DateTimeUnit.HOUR, timeZone)
  .toLocalDateTime(timeZone)

private fun randomSizeBytes() =  (0..100_000_000L).random()
