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

package io.github.sadellie.indexxo.feature.search.component

import androidx.compose.animation.Crossfade
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.search_date_range_selector_between
import indexxo.composeapp.generated.resources.search_date_range_selector_not_set
import io.github.sadellie.indexxo.core.common.localDateTimeNow
import io.github.sadellie.indexxo.core.designsystem.component.DateRangePickerDialog
import io.github.sadellie.indexxo.core.designsystem.component.ListItem
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.CalendarMonth
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded
import io.github.sadellie.indexxo.core.model.LocalDateTimeRange
import io.github.sadellie.indexxo.feature.search.toFormattedString
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSelectorSheet(
  sheetState: SheetState,
  onDismissRequest: () -> Unit,
  title: String,
  dateRange: LocalDateTimeRange?,
  onDateRangeChange: (LocalDateTimeRange?) -> Unit,
) {
  var currentDateRange by remember(dateRange) { mutableStateOf(dateRange) }
  BasicSearchModalSheet(
    title = title,
    sheetState = sheetState,
    onDismissRequest = onDismissRequest,
    onConfirm = { onDateRangeChange(currentDateRange) },
  ) {
    DateRangeSelector(dateRange = currentDateRange, onDateRangeChange = { currentDateRange = it })
  }
}

@Composable
private fun DateRangeSelector(
  dateRange: LocalDateTimeRange?,
  onDateRangeChange: (LocalDateTimeRange?) -> Unit,
) {
  Column {
    ListItem(
      headlineText = stringResource(Res.string.search_date_range_selector_not_set),
      selected = dateRange == null,
      onSelect = { onDateRangeChange(null) },
    )

    ListItem(
      headlineText = stringResource(Res.string.search_date_range_selector_between),
      selected = dateRange != null,
      onSelect = {
        val currentTime = localDateTimeNow()
        onDateRangeChange(currentTime to currentTime)
      },
    )

    Crossfade(dateRange) { targetDateRange ->
      if (targetDateRange != null) {
        OutlinedTextFieldWithDialog(
          zonedDateTime = targetDateRange,
          onDateRangeChange = { onDateRangeChange(it) },
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OutlinedTextFieldWithDialog(
  zonedDateTime: LocalDateTimeRange,
  onDateRangeChange: (LocalDateTimeRange?) -> Unit,
) {
  var showDateRangePicker by remember { mutableStateOf(false) }
  var currentLocalDateTimeRange by remember(zonedDateTime) { mutableStateOf(zonedDateTime) }

  val textFieldValue by
    remember(currentLocalDateTimeRange) {
      val formattedRange = currentLocalDateTimeRange.toFormattedString()
      mutableStateOf(TextFieldValue(formattedRange))
    }

  LaunchedEffect(currentLocalDateTimeRange) { onDateRangeChange(currentLocalDateTimeRange) }

  OutlinedTextField(
    modifier = Modifier.fillMaxWidth(),
    value = textFieldValue,
    onValueChange = {},
    trailingIcon = {
      Icon(
        modifier = Modifier.clickable { showDateRangePicker = true },
        imageVector = SymbolsRounded.CalendarMonth,
        contentDescription = null,
      )
    },
  )

  if (showDateRangePicker) {
    val timeZone = TimeZone.currentSystemDefault()
    val (startDate, endDate) = currentLocalDateTimeRange
    val startDateInMillis: Long = startDate.toInstant(timeZone).toEpochMilliseconds()
    val endDateInMillis: Long = endDate.toInstant(timeZone).toEpochMilliseconds()

    val state = rememberDateRangePickerState(startDateInMillis, endDateInMillis)
    DateRangePickerDialog(
      pickerState = state,
      onConfirm = { start, end ->
        currentLocalDateTimeRange = start to end
        showDateRangePicker = false
      },
      onDismiss = { showDateRangePicker = false },
    )
  }
}

@Composable
@Preview
private fun PreviewDateRangeSelector() {
  DateRangeSelector(dateRange = localDateTimeNow() to localDateTimeNow(), onDateRangeChange = {})
}
