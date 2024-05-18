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

// https://github.com/ButterCam/compose-jetbrains-theme/blob/8f237fd0c144ee8cc425aff5430f48ae634e914e/expui/src/main/kotlin/io/kanro/compose/jetbrains/expui/DesktopPlatform.kt#L10
enum class DesktopPlatform {
  Linux,
  Windows,
  MacOS;

  companion object {
    val Current: DesktopPlatform by lazy {
      val name = System.getProperty("os.name")
      when {
        name?.startsWith("Linux") == true -> Linux
        name?.startsWith("Win") == true -> Windows
        name == "Mac OS X" -> MacOS
        else -> error("Not supported")
      }
    }
  }
}
