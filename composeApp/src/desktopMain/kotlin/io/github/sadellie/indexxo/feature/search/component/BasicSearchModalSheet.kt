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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.confirm
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicSearchModalSheet(
  title: String,
  sheetState: SheetState = rememberModalBottomSheetState(),
  onDismissRequest: () -> Unit,
  onConfirm: () -> Unit,
  content: @Composable () -> Unit,
) {
  ModalBottomSheet(onDismissRequest = onDismissRequest, sheetState = sheetState) {
    Column(
      modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Text(text = title, style = MaterialTheme.typography.titleLarge)
      content()
      Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
          onDismissRequest()
          onConfirm()
        },
      ) {
        Text(stringResource(Res.string.confirm))
      }
    }
  }
}
