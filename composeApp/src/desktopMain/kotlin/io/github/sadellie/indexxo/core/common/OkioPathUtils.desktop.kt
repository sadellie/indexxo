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

import io.github.sadellie.indexxo.core.data.AppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okio.Path

actual suspend fun Path.moveToTrash() {
  withContext(Dispatchers.IO) {
    if (!AppConfig.realDelete) delay(FAKE_DELETE_DURATION_MILLIS) else toFile().moveToTrash()
  }
}

actual fun Path.openInSystem() = toFile().openInSystem()

private const val FAKE_DELETE_DURATION_MILLIS = 100L
