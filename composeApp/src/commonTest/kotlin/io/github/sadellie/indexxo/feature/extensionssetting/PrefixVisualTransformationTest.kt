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

package io.github.sadellie.indexxo.feature.extensionssetting

import androidx.compose.ui.graphics.Color
import io.github.sadellie.indexxo.feature.settings.PrefixVisualTransformation
import kotlin.test.Test
import kotlin.test.assertEquals

class PrefixVisualTransformationTest {

  @Test
  fun testAtStart() {
    transToOrig("|filename.123", "|123")
    transToOrig("file|name.123", "|123")
    transToOrig("filename.|123", "|123")
    transToOrig("filename.1|23", "1|23")
    transToOrig("filename.123|", "123|")
    origToTrans("|123", "filename.|123")
    origToTrans("12|3", "filename.12|3")
    origToTrans("123|", "filename.123|")
  }

  private val expr = PrefixVisualTransformation("filename.", Color.Cyan)

  // Use "|" for cursor
  private fun origToTrans(orig: String, trans: String) {
    val offsetInTrans = trans.indexOf("|")
    val offsetInOrig = orig.indexOf("|")
    val expressionMapping = expr.WithStartOffsetMapping()

    assertEquals(offsetInTrans, expressionMapping.originalToTransformed(offsetInOrig))
  }

  private fun transToOrig(trans: String, orig: String) {
    val offsetInTrans = trans.indexOf("|")
    val offsetInOrig = orig.indexOf("|")
    val expressionMapping = expr.WithStartOffsetMapping()

    assertEquals(offsetInOrig, expressionMapping.transformedToOriginal(offsetInTrans))
  }
}
