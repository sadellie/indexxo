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
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import io.github.sadellie.indexxo.core.database.model.UserPreset
import io.github.sadellie.indexxo.fakeUserPreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
actual fun Screen.getSettingDetailScreenModel(
  presetId: Int
): SettingDetailScreenModel = rememberScreenModel { SettingDetailScreenModel() }

actual class SettingDetailScreenModel : ScreenModel {
  actual val userPreset: StateFlow<UserPreset?> = MutableStateFlow(fakeUserPreset)

  actual fun updateUserPreset(userPreset: UserPreset) = Unit
}
