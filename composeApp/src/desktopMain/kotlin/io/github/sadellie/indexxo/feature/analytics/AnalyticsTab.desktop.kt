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

package io.github.sadellie.indexxo.feature.analytics

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import io.github.sadellie.indexxo.core.common.extension
import io.github.sadellie.indexxo.core.common.localDateTimeNow
import io.github.sadellie.indexxo.core.common.sortByValuesDescending
import io.github.sadellie.indexxo.core.common.stateIn
import io.github.sadellie.indexxo.core.data.IndexedObjectRepository
import io.github.sadellie.indexxo.core.designsystem.theme.Previewer
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObject
import io.github.sadellie.indexxo.core.model.IndexedObjectImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import okio.Path.Companion.toPath

@Composable
actual fun Screen.getAnalyticsTabModel() = koinScreenModel<AnalyticsTabModel>()

@OptIn(ExperimentalCoroutinesApi::class)
actual class AnalyticsTabModel(indexedObjectRepository: IndexedObjectRepository) : ScreenModel {
  private val _chartType = MutableStateFlow(ChartType.ByCategory)
  private val _chartDataByCategory = indexedObjectRepository.indexedObjects.mapLatest { index ->
    val notFolders = index.filter { it.fileCategory != FileCategory.FOLDER }
    val data = notFolders.groupBy { it.fileCategory }.sortByValuesDescending()
    ChartDataByCategory(labels = data.keys.toList(), data = data.values.toList())
  }.flowOn(Dispatchers.Default)

  private val _chartDataByExtension = indexedObjectRepository.indexedObjects.mapLatest { index ->
    val notFolders = index.filter { it.fileCategory != FileCategory.FOLDER }
    val data = notFolders.groupBy { it.path.extension }.sortByValuesDescending()
    val first10 =
      mutableMapOf<String?, Float>().apply {
        val first9 =
          data.keys.take(9).associateWith {
            data[it] ?: return@mapLatest ChartDataByExtension(emptyList(), emptyList())
          }
        putAll(first9)

        val othersCount = data.values.sum() - first9.values.sum()
        if (othersCount > 0) {
          put(null, othersCount)
        }
      }

    ChartDataByExtension(labels = first10.keys.toList(), data = first10.values.toList())
  }.flowOn(Dispatchers.Default)

  private val _largestIndexedObject = indexedObjectRepository.indexedObjects.mapLatest { index ->
    index.maxByOrNull { it.sizeBytes }
  }

  actual fun updateSelectedChartType(chartType: ChartType) = _chartType.update { chartType }

  actual val uiState: StateFlow<AnalyticsTabUIState?> = combine(
    _chartDataByCategory,
    _chartDataByExtension,
    _chartType,
    _largestIndexedObject,
  ) { chartDataByCategory, chartDataByExtension, chartType, largestIndexedObject ->
    AnalyticsTabUIState(
      chartDataByCategory = chartDataByCategory,
      chartDataByExtension = chartDataByExtension,
      selectedChartDataType = chartType,
      largestIndexedObject = largestIndexedObject
    )
  }
    .stateIn(screenModelScope, null)
}

private fun <T> Map<T, List<IndexedObject>>.sortByValuesDescending(): Map<T, Float> =
  this.mapValues { (_, list) -> list.size.toFloat() }.sortByValuesDescending { it.value }

@Composable
@Preview
private fun PreviewAnalyticsTabView() = Previewer {
  AnalyticsTabViewReady(
    modifier = Modifier,
    uiState = AnalyticsTabUIState(
      chartDataByCategory = ChartDataByCategory(
        labels = FileCategory.entries,
        data = List(FileCategory.entries.size) { it.toFloat() },
      ),
      chartDataByExtension = ChartDataByExtension(emptyList(), emptyList()),
      selectedChartDataType = ChartType.ByCategory,
      largestIndexedObject = IndexedObjectImpl(
        path = "test".toPath(),
        parentPath = null,
        sizeBytes = 123456L,
        fileCategory = FileCategory.DOCUMENT,
        createdDate = localDateTimeNow(),
        modifiedDate = localDateTimeNow(),
      ),
    ),
    updateSelectedChartType = {},
  )
}

@Composable
@Preview
private fun PreviewAnalyticsTabViewLoading() = Previewer {
  AnalyticsTabViewLoading(modifier = Modifier)
}
