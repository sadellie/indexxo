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

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.cancel
import indexxo.composeapp.generated.resources.confirm
import org.jetbrains.compose.resources.stringResource

@Composable
fun SimpleAlertDialogConfirmation(
  modifier: Modifier = Modifier,
  title: String,
  icon: @Composable (() -> Unit)? = null,
  text: @Composable (() -> Unit)? = null,
  onConfirm: () -> Unit,
  onDismissRequest: () -> Unit,
  confirmEnabled: Boolean = true,
  confirmLabel: String = stringResource(Res.string.confirm),
  dismissLabel: String = stringResource(Res.string.cancel),
) {
  BasicSimpleAlertDialog(
    modifier = modifier,
    title = title,
    icon = icon,
    text = text,
    onDismissRequest = onDismissRequest,
    confirmButton = {
      SimpleAlertDialogConfirmButton(
        modifier = Modifier.simpleFocusRequest(),
        onConfirm = onConfirm,
        enabled = confirmEnabled,
        label = confirmLabel,
      )
    },
    dismissButton = {
      SimpleAlertDialogDismissButton(
        modifier = Modifier,
        onDismissRequest = onDismissRequest,
        label = dismissLabel,
      )
    },
  )
}

@Composable
fun SimpleAlertDialogDismissOnly(
  modifier: Modifier = Modifier,
  title: String,
  icon: @Composable (() -> Unit)? = null,
  text: @Composable (() -> Unit)? = null,
  onDismissRequest: () -> Unit,
  buttonLabel: String = stringResource(Res.string.cancel),
) {
  BasicSimpleAlertDialog(
    modifier = modifier,
    title = title,
    icon = icon,
    text = text,
    onDismissRequest = onDismissRequest,
    confirmButton = {
      SimpleAlertDialogDismissButton(
        modifier = Modifier.simpleFocusRequest(),
        onDismissRequest = onDismissRequest,
        label = buttonLabel,
      )
    },
  )
}

@Composable
fun SimpleAlertDialogDismissOnly(
  onDismissRequest: () -> Unit,
  title: String,
  modifier: Modifier = Modifier,
  icon: @Composable (() -> Unit)? = null,
  text: @Composable (() -> Unit)? = null,
  dismissButton: @Composable () -> Unit,
) {
  BasicSimpleAlertDialog(
    modifier = modifier,
    title = title,
    icon = icon,
    text = text,
    onDismissRequest = onDismissRequest,
    confirmButton = dismissButton,
  )
}

@Composable
fun SimpleAlertDialogConfirmButton(
  onConfirm: () -> Unit,
  label: String = stringResource(Res.string.confirm),
  enabled: Boolean = true,
  modifier: Modifier = Modifier,
) {
  Button(onConfirm, modifier, enabled) { Text(label) }
}

@Composable
fun SimpleAlertDialogDismissButton(
  onDismissRequest: () -> Unit,
  label: String = stringResource(Res.string.cancel),
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  TextButton(onDismissRequest, modifier, enabled) { Text(label) }
}

@Composable
private fun BasicSimpleAlertDialog(
  modifier: Modifier = Modifier,
  title: String,
  icon: @Composable (() -> Unit)? = null,
  text: @Composable (() -> Unit)? = null,
  onDismissRequest: () -> Unit,
  confirmButton: @Composable () -> Unit,
  dismissButton: @Composable (() -> Unit)? = null,
) {
  AlertDialog(
    onDismissRequest = onDismissRequest,
    confirmButton = confirmButton,
    modifier = modifier,
    dismissButton = dismissButton,
    icon = icon,
    title = { Text(title) },
    text = text,
  )
}

private fun Modifier.simpleFocusRequest() = composed {
  val focusRequester = remember { FocusRequester() }
  LaunchedEffect(Unit) { focusRequester.requestFocus() }
  this.focusRequester(focusRequester)
}
