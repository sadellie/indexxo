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

package io.github.sadellie.indexxo.core.ui

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.discard
import indexxo.composeapp.generated.resources.indexed_object_selector_discard_items
import indexxo.composeapp.generated.resources.indexed_object_selector_discard_items_description
import indexxo.composeapp.generated.resources.move_to_trash_dialog_text
import indexxo.composeapp.generated.resources.move_to_trash_dialog_title
import indexxo.composeapp.generated.resources.move_to_trash_in_progress
import indexxo.composeapp.generated.resources.move_to_trash_refreshing_index
import io.github.sadellie.indexxo.core.designsystem.component.SimpleAlertDialogConfirmation
import io.github.sadellie.indexxo.core.designsystem.component.SimpleAlertDialogDismissButton
import io.github.sadellie.indexxo.core.designsystem.component.SimpleAlertDialogDismissOnly
import io.github.sadellie.indexxo.core.model.MoveToTrashProgress
import org.jetbrains.compose.resources.stringResource

enum class ConfirmationDialogState { MOVE_TO_TRASH, DISCARD }

@Composable
fun MoveToTrashDialogInProgress(
  moveToTrashProgress: MoveToTrashProgress.InProgress,
  onMoveToTrashCancel: () -> Unit,
) {
  SimpleAlertDialogDismissOnly(
    onDismissRequest = {},
    title = stringResource(Res.string.move_to_trash_in_progress),
    text = { Text(moveToTrashProgress.path.toString()) },
    dismissButton = { SimpleAlertDialogDismissButton(onMoveToTrashCancel) },
  )
}

@Composable
fun MoveToTrashDialogRefreshingIndex() {
  SimpleAlertDialogDismissOnly(
    onDismissRequest = {},
    title = stringResource(Res.string.move_to_trash_refreshing_index),
    text = { LinearProgressIndicator() },
    dismissButton = { SimpleAlertDialogDismissButton({}, enabled = false) },
  )
}

@Composable
fun MoveToTrashConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
  SimpleAlertDialogConfirmation(
    title = stringResource(Res.string.move_to_trash_dialog_title),
    text = { Text(stringResource(Res.string.move_to_trash_dialog_text)) },
    onConfirm = onConfirm,
    onDismissRequest = onDismiss,
  )
}

@Composable
fun DiscardConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
  SimpleAlertDialogConfirmation(
    title = stringResource(Res.string.indexed_object_selector_discard_items),
    text = { Text(stringResource(Res.string.indexed_object_selector_discard_items_description)) },
    onConfirm = onConfirm,
    onDismissRequest = onDismiss,
    confirmLabel = stringResource(Res.string.discard),
  )
}
