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

package io.github.sadellie.indexxo.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import co.touchlab.kermit.Logger
import io.github.sadellie.indexxo.core.model.IndexxoPreferences
import io.github.sadellie.indexxo.core.model.ThemingMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapLatest

class PreferencesRepositoryImpl(private val dataStore: DataStore<Preferences>) :
  PreferencesRepository {
  private val data =
    dataStore.data.catch { if (it is IOException) emit(emptyPreferences()) else throw it }

  @OptIn(ExperimentalCoroutinesApi::class)
  override val indexxoPreferencesFlow =
    data.mapLatest { preferences ->
      IndexxoPreferences(
        presetId = preferences[PrefKeys.PRESET_ID] ?: UNSET_PRESET_ID,
        themingMode = preferences.getThemingMode(),
      )
    }

  override suspend fun updatePresetId(presetId: Int?) {
    dataStore.edit { preferences ->
      preferences[PrefKeys.PRESET_ID] = presetId ?: UNSET_PRESET_ID
    }
  }

  override suspend fun updateThemingMode(themingMode: ThemingMode) {
    dataStore.edit { preferences ->
      preferences[PrefKeys.THEMING_MODE] = themingMode.name
    }
  }
}

// -1 means that presetId is not selected (similar to null)
// It's ok to store -1 since ids in database are non-negative integers
private const val UNSET_PRESET_ID = -1

private object PrefKeys {
  val PRESET_ID = intPreferencesKey("PRESET_ID")
  val THEMING_MODE = stringPreferencesKey("THEMING_MODE")
}

fun Preferences.getThemingMode(): ThemingMode = this[PrefKeys.THEMING_MODE]
  ?.letTryOrNull { ThemingMode.valueOf(it) }
  ?: ThemingMode.AUTO

private inline fun <T, R> T.letTryOrNull(block: (T) -> R): R? = try {
  this?.let(block)
} catch (e: Exception) {
  Logger.w(e) { "Failed to get preference value" }
  null
}
