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

package io.github.sadellie.indexxo.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.cancel
import indexxo.composeapp.generated.resources.ok
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
  modifier: Modifier = Modifier,
  pickerState: DateRangePickerState = rememberDateRangePickerState(),
  confirmLabel: String = stringResource(Res.string.ok),
  dismissLabel: String = stringResource(Res.string.cancel),
  onDismiss: () -> Unit = {},
  onConfirm: (LocalDateTime, LocalDateTime) -> Unit,
) {
  BasicAlertDialog(
    onDismissRequest = onDismiss,
    modifier = modifier.wrapContentHeight(),
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Surface(
      modifier = modifier.requiredWidth(360.dp).heightIn(max = 568.dp),
      shape = DatePickerDefaults.shape,
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
    ) {
      Column(verticalArrangement = Arrangement.SpaceBetween) {
        DateRangePicker(modifier = Modifier.weight(1f), state = pickerState)

        Box(modifier = Modifier.align(Alignment.End).padding(DialogButtonsPadding)) {
          AlertDialogFlowRow(
            mainAxisSpacing = DialogButtonsMainAxisSpacing,
            crossAxisSpacing = DialogButtonsCrossAxisSpacing,
          ) {
            TextButton(onClick = onDismiss) { Text(text = dismissLabel) }
            TextButton(
              onClick = {
                val startMillis = pickerState.selectedStartDateMillis ?: return@TextButton
                val endMillis = pickerState.selectedEndDateMillis ?: return@TextButton

                val startDateTime =
                  Instant.fromEpochMilliseconds(startMillis)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                val endDateTime =
                  Instant.fromEpochMilliseconds(endMillis)
                    .toLocalDateTime(TimeZone.currentSystemDefault())

                onConfirm(startDateTime, endDateTime)
              }
            ) {
              Text(text = confirmLabel)
            }
          }
        }
      }
    }
  }
}

// From androidx/compose/material3/AlertDialog.kt
@Composable
private fun AlertDialogFlowRow(
  mainAxisSpacing: Dp,
  crossAxisSpacing: Dp,
  content: @Composable () -> Unit,
) {
  Layout(content) { measurables, constraints ->
    val sequences = mutableListOf<List<Placeable>>()
    val crossAxisSizes = mutableListOf<Int>()
    val crossAxisPositions = mutableListOf<Int>()

    var mainAxisSpace = 0
    var crossAxisSpace = 0

    val currentSequence = mutableListOf<Placeable>()
    var currentMainAxisSize = 0
    var currentCrossAxisSize = 0

    // Return whether the placeable can be added to the current sequence.
    fun canAddToCurrentSequence(placeable: Placeable) =
      currentSequence.isEmpty() ||
        currentMainAxisSize + mainAxisSpacing.roundToPx() + placeable.width <= constraints.maxWidth

    // Store current sequence information and start a new sequence.
    fun startNewSequence() {
      if (sequences.isNotEmpty()) {
        crossAxisSpace += crossAxisSpacing.roundToPx()
      }
      sequences += currentSequence.toList()
      crossAxisSizes += currentCrossAxisSize
      crossAxisPositions += crossAxisSpace

      crossAxisSpace += currentCrossAxisSize
      mainAxisSpace = max(mainAxisSpace, currentMainAxisSize)

      currentSequence.clear()
      currentMainAxisSize = 0
      currentCrossAxisSize = 0
    }

    for (measurable in measurables) {
      // Ask the child for its preferred size.
      val placeable = measurable.measure(constraints)

      // Start a new sequence if there is not enough space.
      if (!canAddToCurrentSequence(placeable)) startNewSequence()

      // Add the child to the current sequence.
      if (currentSequence.isNotEmpty()) {
        currentMainAxisSize += mainAxisSpacing.roundToPx()
      }
      currentSequence.add(placeable)
      currentMainAxisSize += placeable.width
      currentCrossAxisSize = max(currentCrossAxisSize, placeable.height)
    }

    if (currentSequence.isNotEmpty()) startNewSequence()

    val mainAxisLayoutSize = max(mainAxisSpace, constraints.minWidth)

    val crossAxisLayoutSize = max(crossAxisSpace, constraints.minHeight)

    layout(mainAxisLayoutSize, crossAxisLayoutSize) {
      sequences.forEachIndexed { i, placeables ->
        val childrenMainAxisSizes =
          IntArray(placeables.size) { j ->
            placeables[j].width + if (j < placeables.lastIndex) mainAxisSpacing.roundToPx() else 0
          }
        val arrangement = Arrangement.End
        val mainAxisPositions = IntArray(childrenMainAxisSizes.size) { 0 }
        with(arrangement) {
          arrange(mainAxisLayoutSize, childrenMainAxisSizes, layoutDirection, mainAxisPositions)
        }
        placeables.forEachIndexed { j, placeable ->
          placeable.place(x = mainAxisPositions[j], y = crossAxisPositions[i])
        }
      }
    }
  }
}

private val DialogButtonsPadding by lazy { PaddingValues(bottom = 8.dp, end = 6.dp) }
private val DialogButtonsMainAxisSpacing by lazy { 8.dp }
private val DialogButtonsCrossAxisSpacing by lazy { 12.dp }
