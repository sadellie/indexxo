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

import co.touchlab.kermit.Logger
import io.github.sadellie.indexxo.dataDirectory
import java.awt.Desktop
import java.net.URI

actual fun openLink(url: String) {
  val desktop = if (Desktop.isDesktopSupported()) {
    Desktop.getDesktop()
  } else {
    Logger.w { "Desktop is not supported" }
    return
  }

  if (!desktop.isSupported(Desktop.Action.BROWSE)) {
    Logger.w { "Desktop.Action.BROWSE is not supported" }
    return
  }

  try {
    desktop.browse(URI.create(url))
  } catch (e: UnsupportedOperationException) {
    Logger.e { "Failed to browse to $url. Operation is not supported by system" }
  } catch (e: SecurityException) {
    Logger.w(e) { "Security error while browsing to $url" }
  }
}

fun openLicenses() = (resourcesDir / "third-party.html").toFile().openInSystem()

fun openDataFolder() = dataDirectory.toFile().openInSystem()
