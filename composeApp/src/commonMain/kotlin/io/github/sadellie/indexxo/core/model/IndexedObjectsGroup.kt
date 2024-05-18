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

import io.github.sadellie.indexxo.core.common.sortByValuesDescending
import okio.Path
import kotlin.jvm.JvmName

interface IndexedObjectsGroup {
  val duplicates: List<IndexedObject>
  val totalSizeBytes: Long
}

data class DuplicateHash(
  override val duplicates: List<IndexedObject>,
  override val totalSizeBytes: Long,
  val hash: Long,
) : IndexedObjectsGroup

data class DuplicateName(
  override val duplicates: List<IndexedObject>,
  override val totalSizeBytes: Long,
  val name: String,
) : IndexedObjectsGroup

data class SimilarIndexedObjectsGroup(
  override val duplicates: List<IndexedObject>,
  override val totalSizeBytes: Long,
) : IndexedObjectsGroup

@JvmName("duplicateHashListRemove")
fun List<DuplicateHash>.remove(paths: Set<Path>): List<DuplicateHash> =
  this.map { duplicateGroup ->
    val newDuplicates = duplicateGroup.duplicates.removeByPathAndSort(paths)
    duplicateGroup.copy(
      duplicates = newDuplicates,
      totalSizeBytes = newDuplicates.sumOf { it.sizeBytes },
    )
  }
    .commonSortAndFilter()

@JvmName("duplicateNameListRemove")
fun List<DuplicateName>.remove(paths: Set<Path>): List<DuplicateName> =
  this.map { duplicateGroup ->
    val newDuplicates = duplicateGroup.duplicates.removeByPathAndSort(paths)
    duplicateGroup.copy(
      duplicates = newDuplicates,
      totalSizeBytes = newDuplicates.sumOf { it.sizeBytes },
    )
  }
    .commonSortAndFilter()

@JvmName("similarIndexedObjectsGroupListRemove")
fun List<SimilarIndexedObjectsGroup>.remove(paths: Set<Path>): List<SimilarIndexedObjectsGroup> =
  this.map { duplicateGroup ->
    val newDuplicates = duplicateGroup.duplicates.removeByPathAndSort(paths)
    duplicateGroup.copy(
      duplicates = newDuplicates,
      totalSizeBytes = newDuplicates.sumOf { it.sizeBytes },
    )
  }
    .commonSortAndFilter()

@JvmName("duplicateHashListRetain")
fun List<DuplicateHash>.retain(paths: Set<Path>): List<DuplicateHash> =
  this.map { duplicateGroup ->
    val newDuplicates = duplicateGroup.duplicates.retainByPathAndSort(paths)
    duplicateGroup.copy(
      duplicates = newDuplicates,
      totalSizeBytes = newDuplicates.sumOf { it.sizeBytes },
    )
  }
    .commonSortAndFilter()

@JvmName("duplicateNameListRetain")
fun List<DuplicateName>.retain(paths: Set<Path>): List<DuplicateName> =
  this.map { duplicateGroup ->
    val newDuplicates = duplicateGroup.duplicates.retainByPathAndSort(paths)
    duplicateGroup.copy(
      duplicates = newDuplicates,
      totalSizeBytes = newDuplicates.sumOf { it.sizeBytes },
    )
  }
    .commonSortAndFilter()

@JvmName("similarIndexedObjectsGroupListRetain")
fun List<SimilarIndexedObjectsGroup>.retain(paths: Set<Path>): List<SimilarIndexedObjectsGroup> =
  this.map { duplicateGroup ->
    val newDuplicates = duplicateGroup.duplicates.retainByPathAndSort(paths)
    duplicateGroup.copy(
      duplicates = newDuplicates,
      totalSizeBytes = newDuplicates.sumOf { it.sizeBytes },
    )
  }
    .commonSortAndFilter()

/**
 * Sorts by group size, removes cross duplicates and removes empty groups
 */
fun <T : IndexedObject> MutableMap<IndexedObject, MutableList<T>>.cleanUp(): Map<IndexedObject, MutableList<T>> {
  val sortedBySize = this.sortByValuesDescending { it.value.size }

  for ((base, duplicates) in sortedBySize) {
    if (duplicates.isEmpty()) continue

    for (duplicate in duplicates) {
      // find same group but where duplicate is base (reversed)
      val reversedGroup = sortedBySize[duplicate] ?: continue
      if (reversedGroup.isEmpty()) continue

      val baseInReversedGroup = reversedGroup.indexOfFirst { it == base }
      if (baseInReversedGroup == -1) continue
      reversedGroup.removeAt(baseInReversedGroup)
    }
  }

  return sortedBySize.filter { it.value.isNotEmpty() }
}

private fun List<IndexedObject>.removeByPathAndSort(paths: Set<Path>): List<IndexedObject> =
  this.filter { it.path !in paths }.sortedBy { it.createdDate }

private fun List<IndexedObject>.retainByPathAndSort(paths: Set<Path>): List<IndexedObject> =
  this.filter { it.path in paths }.sortedBy { it.createdDate }

private fun <T : IndexedObjectsGroup> List<T>.commonSortAndFilter(): List<T> =
  this.filter { it.duplicates.size > 1 } // remove if only one item is left in group
    .sortedByDescending { it.duplicates.size }
