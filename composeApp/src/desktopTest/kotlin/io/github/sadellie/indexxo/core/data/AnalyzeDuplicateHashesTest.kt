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

import io.github.sadellie.indexxo.core.model.DuplicateHash
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import java.io.File
import kotlin.test.Test

class AnalyzeDuplicateHashesTest {
  private val partialHashSampleSizeBytes = 32
  private val folder1 = File("./testDirs/duplicateHashesTest").toIndexedObjectImplFolder()
  private val file1 = File("./testDirs/duplicateHashesTest/file1.txt").toIndexedObjectImplDocument()
  private val file2 = File("./testDirs/duplicateHashesTest/file2.txt").toIndexedObjectImplDocument()
  private val file3 = File("./testDirs/duplicateHashesTest/file3.txt").toIndexedObjectImplDocument()
  private val file4 = File("./testDirs/duplicateHashesTest/file4.txt").toIndexedObjectImplDocument()
  private val file5 = File("./testDirs/duplicateHashesTest/file5.txt").toIndexedObjectImplDocument()
  private val fakeIndex = listOf(folder1, file1, file2, file3, file4, file5)

  @Test
  fun fullIndexTest() = runBlocking {
    val expectedProblemItems =
      listOf(
        DuplicateHash(
          duplicates = listOf(file1, file2, file3),
          hash = 2923350487,
          totalSizeBytes = 384,
        ),
      )
    val (actualProblemItems, _) = analyzeDuplicateHashes(fakeIndex, partialHashSampleSizeBytes) {}

    assertEquals(expectedProblemItems, actualProblemItems)
  }
}
