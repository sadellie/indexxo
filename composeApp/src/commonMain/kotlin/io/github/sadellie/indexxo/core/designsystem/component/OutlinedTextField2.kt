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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.CodepointTransformation
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OutlinedTextField2(
  value: TextFieldState,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  readOnly: Boolean = false,
  textStyle: TextStyle = LocalTextStyle.current,
  label: @Composable (() -> Unit)? = null,
  placeholder: @Composable (() -> Unit)? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  prefix: @Composable (() -> Unit)? = null,
  suffix: @Composable (() -> Unit)? = null,
  supportingText: @Composable (() -> Unit)? = null,
  isError: Boolean = false,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
  onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  cursorBrush: Brush = SolidColor(Color.Black),
  codepointTransformation: CodepointTransformation? = null,
  scrollState: ScrollState = rememberScrollState(),
  shape: Shape = OutlinedTextFieldDefaults.shape,
  colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
) {
  // If color is not provided via the text style, use content color as a default
  val textColor =
    textStyle.color.takeOrElse { textColor(enabled, isError, interactionSource).value }
  val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

  CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
    BasicTextField2(
      state = value,
      modifier =
        if (label != null) {
            modifier
              // Merge semantics at the beginning of the modifier chain to ensure padding is
              // considered part of the text field.
              .semantics(mergeDescendants = true) {}
              .padding(top = 8.dp)
          } else {
            modifier
          }
          .defaultErrorSemantics(isError, "Error")
          .defaultMinSize(
            minWidth = OutlinedTextFieldDefaults.MinWidth,
            minHeight = OutlinedTextFieldDefaults.MinHeight,
          ),
      enabled = enabled,
      readOnly = readOnly,
      inputTransformation = null,
      textStyle = mergedTextStyle,
      keyboardOptions = keyboardOptions,
      keyboardActions = keyboardActions,
      lineLimits = lineLimits,
      onTextLayout = onTextLayout,
      interactionSource = interactionSource,
      cursorBrush = cursorBrush,
      codepointTransformation = codepointTransformation,
      decorator = { innerTextField ->
        OutlinedTextFieldDefaults.DecorationBox(
          value = value.text.toString(),
          visualTransformation = visualTransformation,
          innerTextField = innerTextField,
          placeholder = placeholder,
          label = label,
          leadingIcon = leadingIcon,
          trailingIcon = trailingIcon,
          prefix = prefix,
          suffix = suffix,
          supportingText = supportingText,
          singleLine = lineLimits == TextFieldLineLimits.SingleLine,
          enabled = enabled,
          isError = isError,
          interactionSource = interactionSource,
          colors = colors,
          container = {
            OutlinedTextFieldDefaults.ContainerBox(
              enabled,
              isError,
              interactionSource,
              colors,
              shape,
            )
          },
        )
      },
      scrollState = scrollState,
    )
  }
}

private fun Modifier.defaultErrorSemantics(
  isError: Boolean,
  defaultErrorMessage: String,
): Modifier = if (isError) semantics { error(defaultErrorMessage) } else this

@Composable
private fun textColor(
  enabled: Boolean,
  isError: Boolean,
  interactionSource: InteractionSource,
): State<Color> {
  val focused by interactionSource.collectIsFocusedAsState()

  val targetValue =
    when {
      !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
      isError -> MaterialTheme.colorScheme.onSurface
      focused -> MaterialTheme.colorScheme.onSurface
      else -> MaterialTheme.colorScheme.onSurface
    }
  return rememberUpdatedState(targetValue)
}
