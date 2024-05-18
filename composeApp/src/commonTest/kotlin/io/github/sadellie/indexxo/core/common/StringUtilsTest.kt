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

import kotlin.test.Test
import kotlin.test.assertEquals

class StringUtilsTest {
  @Test
  fun testLessThan1KB() {
    assertEquals("10 B", formatBytes(10))
  }

  @Test
  fun test42KB() {
    assertEquals("42 KB", formatBytes(43_066L))
  }

  @Test
  fun test25_8MB() {
    assertEquals("25.89 MB", formatBytes(27_144_042L))
  }

  @Test
  fun test2_5GB() {
    assertEquals("2.25 GB", formatBytes(2_410_971_136L))
  }
}
