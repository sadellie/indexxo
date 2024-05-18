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

import kotlin.math.pow
import kotlin.math.roundToInt

fun formatBytes(bytes: Long): String =
  when {
    bytes < BYTES_IN_KILOBYTE -> "$bytes B"
    bytes < BYTES_IN_MEGABYTE -> "${(bytes.toDouble() / BYTES_IN_KILOBYTE).roundToInt()} KB"
    bytes < BYTES_IN_GIGABYTE -> "${(bytes.toDouble() / BYTES_IN_MEGABYTE).roundTo(2)} MB"
    else -> "${(bytes.toDouble() / BYTES_IN_GIGABYTE).roundTo(2)} GB"
  }

private fun Double.roundTo(numFractionDigits: Int): Double {
  val factor = 10.0.pow(numFractionDigits.toDouble())
  return (this * factor).roundToInt() / factor
}

fun Float.toPercent(): String = "${times(FLOAT_TO_PERCENT).roundToInt()}%"

private const val BYTES_IN_KILOBYTE = 1_024
private const val BYTES_IN_MEGABYTE = 1_048_576
private const val BYTES_IN_GIGABYTE = 1_073_741_824L
private const val FLOAT_TO_PERCENT = 100
