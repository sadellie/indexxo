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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.sadellie.indexxo.core.designsystem.component.EmptyScreen

actual object SearchScreen : Screen {
  private fun readResolve(): Any = SearchScreen

  @Composable
  actual override fun Content() {
    val localNavigator = LocalNavigator.currentOrThrow
    val screenModel = koinScreenModel<SearchScreenModel>()
    when (val uiState = screenModel.uiState.collectAsState().value) {
      null -> EmptyScreen()
      else ->
        SearchScreenViewReady(
          uiState = uiState,
          textQuery = screenModel.textQuery,
          onTextQueryChange = screenModel::onTextQueryChange,
          onSearchOptionsChange = screenModel::onSearchOptionsChange,
          navigateUp = localNavigator::pop,
        )
    }
  }
}
