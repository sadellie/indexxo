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

package io.github.sadellie.indexxo.feature.home

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import io.github.sadellie.indexxo.core.common.stateIn
import io.github.sadellie.indexxo.core.data.UserPresetsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

@Composable actual fun Screen.getHomeScreenModel() = koinScreenModel<HomeScreenModel>()

actual class HomeScreenModel(
  userPresetsRepository: UserPresetsRepository,
) : ScreenModel {
  @OptIn(ExperimentalCoroutinesApi::class)
  actual val presetName =
    userPresetsRepository.currentUserPreset
      .mapLatest { it?.name }
      .stateIn(screenModelScope, null)
}
