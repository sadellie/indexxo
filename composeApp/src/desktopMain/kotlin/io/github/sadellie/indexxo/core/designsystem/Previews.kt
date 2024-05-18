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

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.loading
import indexxo.composeapp.generated.resources.name
import indexxo.composeapp.generated.resources.search_empty_results
import io.github.sadellie.indexxo.core.designsystem.component.AnnoyingBox
import io.github.sadellie.indexxo.core.designsystem.component.AssistChip
import io.github.sadellie.indexxo.core.designsystem.component.DropDownChip
import io.github.sadellie.indexxo.core.designsystem.component.FilterChip
import io.github.sadellie.indexxo.core.designsystem.component.ListHeader
import io.github.sadellie.indexxo.core.designsystem.component.ListItem
import io.github.sadellie.indexxo.core.designsystem.component.OutlinedTextField2
import io.github.sadellie.indexxo.core.designsystem.component.SuggestionChip
import io.github.sadellie.indexxo.core.designsystem.component.Table
import io.github.sadellie.indexxo.core.designsystem.component.TableCell
import io.github.sadellie.indexxo.core.designsystem.component.TableColumnInfo
import io.github.sadellie.indexxo.core.designsystem.component.TableColumnSort
import io.github.sadellie.indexxo.core.designsystem.component.TableRow
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.AccessibilityNew
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Check
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Home
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Settings
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded
import org.jetbrains.compose.resources.stringResource

@Preview
@Composable
private fun PreviewAnnoyingBox() {
  AnnoyingBox(
    modifier = Modifier.fillMaxWidth().padding(16.dp),
    imageVector = SymbolsRounded.AccessibilityNew,
    imageVectorContentDescription = "",
    title = "Title text",
    support = "Lorem ipsum or something",
    actionTitle = "Close",
    onActionClick = {},
  )
}

@Preview
@Composable
private fun PreviewChip() {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    AssistChip(onClick = {}, imageVector = SymbolsRounded.Settings, contentDescription = "")

    SuggestionChip(label = "suggestion")

    FilterChip(
      selected = true,
      onClick = {},
      label = "filter",
      imageVector = SymbolsRounded.Check,
      contentDescription = "",
    )

    FilterChip(
      selected = true,
      onClick = {},
      label = "filter no icon",
    )

    DropDownChip(onClick = {}, label = "dropdown", contentDescription = "", active = true)
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
private fun PreviewOutlinedTextField2() {
  OutlinedTextField2(value = TextFieldState("This is a test"))
}

@Preview
@Composable
private fun PreviewListItem1() {
  Column {
    ListItem(
      modifier = Modifier,
      headlineText = "Headline",
      supportingText = "Support",
      checked = true,
      onCheckedChange = {},
      onAdditionalContentClick = {},
    )

    HorizontalDivider()

    ListItem(
      modifier = Modifier,
      headlineContent = { Text("Headline") },
      supportingContent = { Text("Support") },
      leadingContent = {
        Icon(
          imageVector = SymbolsRounded.Home,
          contentDescription = null,
          modifier = Modifier.size(24.dp),
        )
      },
    )

    HorizontalDivider()

    ListItem(
      modifier = Modifier,
      headlineContent = { Text("Headline") },
      leadingContent = {
        RadioButton(selected = false, onClick = {}, modifier = Modifier.size(24.dp))
      },
    )

    HorizontalDivider()

    ListItem(
      modifier = Modifier,
      headlineText = "Text text",
      supportingText = "Support text support text support text support text",
      icon = SymbolsRounded.Home,
      iconDescription = "",
      trailing = {},
    )

    HorizontalDivider()

    ListItem(
      modifier = Modifier,
      headlineText = "Text text",
      icon = SymbolsRounded.Home,
      supportingText = "Support text support text support text support text",
      switchState = true,
      onSwitchChange = {},
    )

    HorizontalDivider()

    ListItem(
      modifier = Modifier,
      headlineText = "Text text",
      supportingText = "Support text support text support text support text",
      selected = true,
      onSelect = {},
    )
  }
}

@Composable
@Preview
private fun PreviewListHeader() {
  ListHeader(text = "Header")
}

@Preview
@Composable
private fun PreviewPixelSwitch() {
  var checked by remember { mutableStateOf(false) }
  androidx.compose.material3.Switch(checked = checked, onCheckedChange = { checked = !checked })
}

@Composable
@Preview
private fun PreviewTable() {
  data class SomeClass(val value1: String, val value2: String, val value3: String)

  val items = List(5) { SomeClass("Value 1: $it", "Value 2: $it", "Value 3: $it") }
  val tableColumnInfos =
    listOf(
      TableColumnInfo(
        name = Res.string.name,
        width = 140.dp,
        draggable = false,
        sortState = TableColumnSort.ACTIVE_ASCENDING,
      ),
      TableColumnInfo(
        name = Res.string.name,
        width = 120.dp,
        draggable = true,
        sortState = TableColumnSort.ACTIVE_DESCENDING,
      ),
      TableColumnInfo(
        name = Res.string.name,
        width = 100.dp,
        draggable = false,
        sortState = TableColumnSort.DISABLED,
      ),
    )

  Table(
    items = items,
    itemKey = { _, item -> item.hashCode() },
    tableColumnInfos = tableColumnInfos,
    onTableColumnInfosChange = {},
    row = { _, item ->
      TableRow {
        TableCell(
          modifier = Modifier.border(1.dp, Color.Red).width(tableColumnInfos[0].width),
          text = item.value1,
        )
        TableCell(
          modifier = Modifier.border(1.dp, Color.Red).width(tableColumnInfos[1].width),
          text = item.value2,
        )
        TableCell(
          modifier = Modifier.border(1.dp, Color.Red).width(tableColumnInfos[2].width),
          text = item.value3,
        )
      }
    },
    emptyPlaceholder = { Text(stringResource(Res.string.search_empty_results)) },
    loadingPlaceholder = { Text(stringResource(Res.string.loading)) },
  )
}
