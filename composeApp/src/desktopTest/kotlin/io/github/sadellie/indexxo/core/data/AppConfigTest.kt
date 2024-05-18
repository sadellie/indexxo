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

import co.touchlab.kermit.Severity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class AppConfigTest {
  @Test
  fun parseArgs() {
    val args = arrayOf(
      "--test", "value", // garbage should cause no issues
      "--buildType", "buildTypeValue",
      "--customDataFolder", "customDataFolderValue",
      "--realDelete", "true",
      "--logSeverity", "2",
      "--logToFile", "true",
    )

    AppConfig.loadFromArgs(args)

    assertEquals("buildTypeValue", AppConfig.buildType)
    assertEquals("buildTypeValue", AppConfig.buildType)
    assertEquals("customDataFolderValue", AppConfig.customDataFolder)
    assertEquals(true, AppConfig.realDelete)
    assertEquals(Severity.Info, AppConfig.logSeverity)
    assertEquals(true, AppConfig.logToFile)
  }

  @Test
  fun invalidArgs() {
    val args = arrayOf(
      "--logSeverity", "something that can not be parsed as Severity",
    )

    assertFails {
      AppConfig.loadFromArgs(args)
    }
  }
}
