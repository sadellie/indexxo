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

package io.github.sadellie.indexxo.feature.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.search_text_field_placeholder
import io.github.sadellie.indexxo.core.designsystem.ProvideColor
import io.github.sadellie.indexxo.core.designsystem.component.NavigateUpButton
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Close
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Search
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
  modifier: Modifier = Modifier,
  query: TextFieldValue,
  onQueryChange: (TextFieldValue) -> Unit,
  navigateUp: () -> Unit,
  focusManager: FocusManager = LocalFocusManager.current,
  onSearch: KeyboardActionScope.() -> Unit = { focusManager.clearFocus() },
  focusRequester: FocusRequester = remember { FocusRequester() },
  trailingIcon: @Composable () -> Unit = { SearchButton { focusManager.clearFocus() } },
  scrollBehavior: TopAppBarScrollBehavior,
) {
  val notEmpty = remember(query) { query.text.isNotEmpty() }
  fun clear() = onQueryChange(TextFieldValue())
//  LaunchedEffect(Unit) { focusRequester.requestFocus() }
//  LaunchedEffect(scrollBehavior.state.overlappedFraction) {
//    if (scrollBehavior.state.collapsedFraction > 0.5f) focusManager.clearFocus()
//  }

  val heightOffsetLimit =
    with(LocalDensity.current) { -(SearchBarTokens.SearchBarFullHeight).toPx() }
  SideEffect {
    if (scrollBehavior.state.heightOffsetLimit != heightOffsetLimit) {
      scrollBehavior.state.heightOffsetLimit = heightOffsetLimit
    }
  }
  val height =
    LocalDensity.current.run {
      SearchBarTokens.SearchBarFullHeight + scrollBehavior.state.heightOffset.toDp()
    }

  Box(
    modifier = modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets).height(height),
    contentAlignment = Alignment.Center,
  ) {
    Row(
      modifier =
        Modifier.offset { IntOffset(x = 0, y = scrollBehavior.state.heightOffset.roundToInt()) }
          .padding(horizontal = SearchBarTokens.SearchBarHorizontalPadding)
          .requiredHeight(SearchBarTokens.SearchBarHeight)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp))
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      ProvideColor(MaterialTheme.colorScheme.onSurface) {
        NavigateUpButton { if (notEmpty) clear() else navigateUp() }

        SearchTextField(
          modifier = Modifier.focusRequester(focusRequester).fillMaxWidth().weight(1f),
          value = query,
          placeholder = stringResource(Res.string.search_text_field_placeholder),
          onValueChange = onQueryChange,
          onSearch = onSearch,
        )

        ClearButton(notEmpty, ::clear)

        trailingIcon()
      }
    }
  }
}

@Composable
private fun SearchTextField(
  modifier: Modifier,
  value: TextFieldValue,
  placeholder: String,
  onValueChange: (TextFieldValue) -> Unit,
  onSearch: KeyboardActionScope.() -> Unit,
) {
  BasicTextField(
    modifier = modifier,
    value = value,
    onValueChange = onValueChange,
    singleLine = true,
    textStyle =
      MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    keyboardActions = KeyboardActions(onSearch = onSearch),
    decorationBox = { innerTextField ->
      innerTextField()
      // Showing placeholder only when the query is empty
      value.text.ifEmpty {
        Text(
          modifier = Modifier.alpha(SearchBarTokens.SEARCH_BAR_PLACEHOLDER_TEXT_ALPHA),
          text = placeholder,
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurface,
        )
      }
    },
  )
}

@Composable
private fun SearchButton(onClick: () -> Unit) {
  SearchBarIconButton(onClick) {
    Icon(imageVector = SymbolsRounded.Search, contentDescription = null)
  }
}

@Composable
private fun ClearButton(visible: Boolean, onClick: () -> Unit) {
  AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
    SearchBarIconButton(onClick) {
      Icon(imageVector = SymbolsRounded.Close, contentDescription = null)
    }
  }
}

@Composable
private fun SearchBarIconButton(onClick: () -> Unit, content: @Composable () -> Unit) {
  Box(
    modifier =
      Modifier.size(24.dp)
        .clickable(
          onClick = onClick,
          enabled = true,
          role = Role.Button,
          interactionSource = remember { MutableInteractionSource() },
          indication = rememberRipple(bounded = false, radius = 20.dp),
        ),
    contentAlignment = Alignment.Center,
  ) {
    content()
  }
}

private object SearchBarTokens {
  val SearchBarHeight = 56.dp
  val SearchBarHorizontalPadding = 16.dp
  val SearchBarVerticalPadding = 8.dp
  val SearchBarFullHeight = SearchBarHeight + SearchBarVerticalPadding * 2
  const val SEARCH_BAR_PLACEHOLDER_TEXT_ALPHA = 0.7f
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun SearchBarPreview() {
  SearchBar(
    query = TextFieldValue("test"),
    onQueryChange = {},
    navigateUp = {},
    scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
  )
}
