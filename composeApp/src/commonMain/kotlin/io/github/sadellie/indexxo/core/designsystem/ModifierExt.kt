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

package io.github.sadellie.indexxo.core.designsystem

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

expect fun Modifier.wResizeCursor(): Modifier

fun Modifier.tableBorders(
  paddingValues: PaddingValues = PaddingValues(
    start = 16.dp,
    top = 0.dp,
    end = 16.dp,
    bottom = 16.dp,
  )
): Modifier = composed {
  val shapeMask = RoundedCornerShape(12.dp)
  this
    .padding(paddingValues)
    .clip(shapeMask)
    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shapeMask)
}
