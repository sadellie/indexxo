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

package io.github.sadellie.indexxo.feature.common

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import io.github.sadellie.indexxo.core.common.stateIn
import io.github.sadellie.indexxo.core.data.UserPresetsRepository
import io.github.sadellie.indexxo.core.database.model.UserPreset
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

@Composable
actual fun Screen.getSettingDetailScreenModel(presetId: Int): SettingDetailScreenModel =
  koinScreenModel { parametersOf(presetId) }

actual class SettingDetailScreenModel(
  private val userPresetsRepository: UserPresetsRepository,
): ScreenModel {
  actual val userPreset = userPresetsRepository.currentUserPreset
    .stateIn(screenModelScope, null)

  actual fun updateUserPreset(userPreset: UserPreset) {
    screenModelScope.launch { userPresetsRepository.updateUserPreset(userPreset) }
  }
}

