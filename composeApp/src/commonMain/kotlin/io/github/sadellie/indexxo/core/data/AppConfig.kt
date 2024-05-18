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
import io.github.sadellie.indexxo.BuildKonfig

data object AppConfig {
  var buildType = BuildKonfig.buildType
    private set

  val appVersion = BuildKonfig.appVersion

  val appVersionName = BuildKonfig.appVersionName

  var customDataFolder = BuildKonfig.customDataFolder
    private set

  var realDelete = BuildKonfig.realDelete
    private set

  var logSeverity = Severity.entries[BuildKonfig.logSeverity]
    private set

  var logToFile = BuildKonfig.logToFile
    private set

  fun loadFromArgs(args: Array<String>) {
    val mapOfArgs = args.toList().chunked(2).associate { it[0].removePrefix("--") to it[1] }

    val buildTypeArg = mapOfArgs["buildType"]
    if (!buildTypeArg.isNullOrBlank()) {
      buildType = buildTypeArg
    }

    val customDataFolderArg = mapOfArgs["customDataFolder"]
    if (!customDataFolderArg.isNullOrBlank()) {
      customDataFolder = customDataFolderArg
    }

    val realDeleteArg = mapOfArgs["realDelete"]
    if (!realDeleteArg.isNullOrBlank()) {
      realDelete = realDeleteArg.toBoolean()
    }

    val logSeverityArg = mapOfArgs["logSeverity"]
    if (!logSeverityArg.isNullOrBlank()) {
      logSeverity = Severity.entries[logSeverityArg.toInt()]
    }

    val logToFileArg = mapOfArgs["logToFile"]
    if (!logToFileArg.isNullOrBlank()) {
      logToFile = logToFileArg.toBoolean()
    }
  }
}
