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

package io.github.sadellie.indexxo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import coil3.ImageLoader
import coil3.PlatformContext
import io.github.sadellie.indexxo.core.data.IndexedObjectRepository
import io.github.sadellie.indexxo.core.data.IndexedObjectRepositoryImpl
import io.github.sadellie.indexxo.core.data.PreferencesRepository
import io.github.sadellie.indexxo.core.data.PreferencesRepositoryImpl
import io.github.sadellie.indexxo.core.data.UserPresetsRepository
import io.github.sadellie.indexxo.core.data.UserPresetsRepositoryImpl
import io.github.sadellie.indexxo.core.database.IndexxoDatabase
import io.github.sadellie.indexxo.core.database.MAIN_DB_NAME
import io.github.sadellie.indexxo.core.database.getIndexxoDatabase
import io.github.sadellie.indexxo.core.datastore.DS_NAME
import io.github.sadellie.indexxo.core.datastore.createDataStore
import io.github.sadellie.indexxo.feature.actions.ActionsTabModel
import io.github.sadellie.indexxo.feature.analytics.AnalyticsTabModel
import io.github.sadellie.indexxo.feature.common.SettingDetailScreenModel
import io.github.sadellie.indexxo.feature.duplicatefilenames.DuplicateFileNamesScreenModel
import io.github.sadellie.indexxo.feature.duplicatefoldernames.DuplicateFolderNamesScreenModel
import io.github.sadellie.indexxo.feature.duplicatehashes.DuplicateHashesScreenModel
import io.github.sadellie.indexxo.feature.emptyfiles.EmptyFilesScreenModel
import io.github.sadellie.indexxo.feature.emptyfolders.EmptyFoldersScreenModel
import io.github.sadellie.indexxo.feature.export.ExportTabModel
import io.github.sadellie.indexxo.feature.home.HomeScreenModel
import io.github.sadellie.indexxo.feature.loader.LoaderScreenModel
import io.github.sadellie.indexxo.feature.search.SearchScreenModel
import io.github.sadellie.indexxo.feature.settings.SettingsScreenModel
import io.github.sadellie.indexxo.feature.similarimages.SimilarImagesScreenModel
import io.github.sadellie.indexxo.feature.similarvideos.SimilarVideosScreenModel
import io.github.sadellie.indexxo.feature.userpresets.UserPresetsScreenModel
import io.github.sadellie.indexxo.feature.warnings.WarningsTabModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.lazyModule

@OptIn(KoinExperimentalAPI::class)
val userData = lazyModule {
  single<IndexxoDatabase> { getIndexxoDatabase(dataDirectory / MAIN_DB_NAME) }
  single<DataStore<Preferences>> { createDataStore(dataDirectory / DS_NAME) }
}

@OptIn(KoinExperimentalAPI::class)
val repositories = lazyModule {
  single<IndexedObjectRepository> { IndexedObjectRepositoryImpl() }

  single<PreferencesRepository> { PreferencesRepositoryImpl(dataStore = get()) }

  factory<UserPresetsRepository> {
    UserPresetsRepositoryImpl(dao = get<IndexxoDatabase>().dao(), preferencesRepository = get())
  }
}

@OptIn(KoinExperimentalAPI::class)
val coilImageLoader = lazyModule {
  single {
    ImageLoader.Builder(PlatformContext.INSTANCE)
      .components { add(ThumbnailFromVideoFetcher.Factory()) }
      .build()
  }
}

@OptIn(KoinExperimentalAPI::class)
val screenModels = lazyModule {
  factory<DialogSettingsScreenModel> {
    DialogSettingsScreenModel(preferencesRepository = get())
  }

  factory<SettingsScreenModel> {
    SettingsScreenModel(userPresetsRepository = get(), indexedObjectRepository = get())
  }

  factory<UserPresetsScreenModel> {
    UserPresetsScreenModel(userPresetsRepository = get(), preferencesRepository = get())
  }

  factory<SettingDetailScreenModel> {
    SettingDetailScreenModel(userPresetsRepository = get())
  }

  factory<LoaderScreenModel> {
    LoaderScreenModel(userPreset = get(), indexedObjectRepository = get())
  }

  factory<HomeScreenModel> {
    HomeScreenModel(userPresetsRepository = get())
  }

  factory<SearchScreenModel> {
    SearchScreenModel(userPresetsRepository = get(), indexedObjectRepository = get())
  }

  factory<ActionsTabModel> {
    ActionsTabModel(userPresetsRepository = get(), indexedObjectRepository = get())
  }
  factory<AnalyticsTabModel> { AnalyticsTabModel(indexedObjectRepository = get()) }
  factory<ExportTabModel> { ExportTabModel(indexedObjectRepository = get()) }
  factory<WarningsTabModel> { WarningsTabModel(indexedObjectRepository = get()) }
  factory<SimilarImagesScreenModel> { SimilarImagesScreenModel(indexedObjectRepository = get()) }
  factory<SimilarVideosScreenModel> { SimilarVideosScreenModel(indexedObjectRepository = get()) }
  factory<DuplicateHashesScreenModel> { DuplicateHashesScreenModel(indexedObjectRepository = get()) }
  factory<DuplicateFileNamesScreenModel> { DuplicateFileNamesScreenModel(indexedObjectRepository = get()) }
  factory<DuplicateFolderNamesScreenModel> { DuplicateFolderNamesScreenModel(indexedObjectRepository = get()) }
  factory<EmptyFilesScreenModel> { EmptyFilesScreenModel(indexedObjectRepository = get()) }
  factory<EmptyFoldersScreenModel> { EmptyFoldersScreenModel(indexedObjectRepository = get()) }
}
