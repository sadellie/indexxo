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

import io.github.sadellie.indexxo.core.common.maxSystemThreads
import io.github.sadellie.indexxo.core.model.SimilarIndexedObjectsGroup
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class AnalyzeSimilarImagesTest {
  private val car0 = "./testDirs/analyzeSimilarImagesTest/car0.jpg".toIndexedObject()
  private val car1 = "./testDirs/analyzeSimilarImagesTest/car1.jpg".toIndexedObject()
  private val soap0 = "./testDirs/analyzeSimilarImagesTest/soap0.jpg".toIndexedObject()
  private val soap1 = "./testDirs/analyzeSimilarImagesTest/soap1.jpg".toIndexedObject()
  private val soap2 = "./testDirs/analyzeSimilarImagesTest/soap2.jpg".toIndexedObject()
  private val sydney0 = "./testDirs/analyzeSimilarImagesTest/sydney0.jpg".toIndexedObject()
  private val sydney1 = "./testDirs/analyzeSimilarImagesTest/sydney1.jpg".toIndexedObject()
  private val tower0 = "./testDirs/analyzeSimilarImagesTest/tower0.jpg".toIndexedObject()
  private val tower1 = "./testDirs/analyzeSimilarImagesTest/tower1.jpg".toIndexedObject()
  private val firework0 = "./testDirs/analyzeSimilarImagesTest/firework0.jpg".toIndexedObject()
  private val firework1 = "./testDirs/analyzeSimilarImagesTest/firework1.jpg".toIndexedObject()
  private val firework2 = "./testDirs/analyzeSimilarImagesTest/firework2.jpg".toIndexedObject()
  private val firework3 = "./testDirs/analyzeSimilarImagesTest/firework3.jpg".toIndexedObject()
  private val city0 = "./testDirs/analyzeSimilarImagesTest/city0.jpg".toIndexedObject()
  private val city1 = "./testDirs/analyzeSimilarImagesTest/city1.jpg".toIndexedObject()
  private val fisheye0 = "./testDirs/analyzeSimilarImagesTest/fisheye0.jpg".toIndexedObject()
  private val fisheye1 = "./testDirs/analyzeSimilarImagesTest/fisheye1.jpg".toIndexedObject()
  private val fisheye2 = "./testDirs/analyzeSimilarImagesTest/fisheye2.jpg".toIndexedObject()
  private val fisheye3 = "./testDirs/analyzeSimilarImagesTest/fisheye3.jpg".toIndexedObject()
  private val fisheye4 = "./testDirs/analyzeSimilarImagesTest/fisheye4.jpg".toIndexedObject()
  private val sun0 = "./testDirs/analyzeSimilarImagesTest/sun0.jpg".toIndexedObject()
  private val sun1 = "./testDirs/analyzeSimilarImagesTest/sun1.jpg".toIndexedObject()

  private val index = listOf(
    car0, car1,
    soap0, soap1, soap2,
    sydney0, sydney1,
    tower0, tower1,
    firework0, firework1, firework2, firework3,
    city0, city1,
    fisheye0, fisheye1, fisheye2, fisheye3, fisheye4,
    sun0, sun1,
  )

  @Test
  fun testGroups() = runBlocking {
    val expected = listOf(
      SimilarIndexedObjectsGroup(
        duplicates = listOf(soap0, soap1, soap2),
        totalSizeBytes = 86_141L,
      ),
      SimilarIndexedObjectsGroup(
        duplicates = listOf(car0, car1),
        totalSizeBytes = 290_253L,
      ),
      SimilarIndexedObjectsGroup(
        duplicates = listOf(tower0, tower1),
        totalSizeBytes = 229_584L,
      ),
      SimilarIndexedObjectsGroup(
        duplicates = listOf(sun0, sun1),
        totalSizeBytes = 214_820L,
      ),
    )
    val actual = analyzeSimilarImages(
      indexedObjects = index,
      minSimilarity = 0.8f,
      compareColors = true,
      maxThreads = maxSystemThreads,
      callback = {},
    ).first

    assertEquals(expected, actual)
  }
}
