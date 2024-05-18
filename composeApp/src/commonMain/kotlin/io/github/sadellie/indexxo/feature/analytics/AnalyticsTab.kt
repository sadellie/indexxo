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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.analytics_tab_largest_file
import indexxo.composeapp.generated.resources.analytics_tab_title
import indexxo.composeapp.generated.resources.extensions
import indexxo.composeapp.generated.resources.file_categories
import indexxo.composeapp.generated.resources.loading
import indexxo.composeapp.generated.resources.other
import io.github.koalaplot.core.pie.DefaultSlice
import io.github.koalaplot.core.pie.PieChart
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.generateHueColorPalette
import io.github.sadellie.indexxo.core.common.formatBytes
import io.github.sadellie.indexxo.core.designsystem.component.FileThumbnail
import io.github.sadellie.indexxo.core.designsystem.component.RowWithScrollbar
import io.github.sadellie.indexxo.core.designsystem.component.ScaffoldWithTopBar
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.DataUsage
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded
import io.github.sadellie.indexxo.core.model.FileCategory
import io.github.sadellie.indexxo.core.model.IndexedObject
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

data object AnalyticsTab : Tab {
  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val screenModel = getAnalyticsTabModel()

    ScaffoldWithTopBar(title = options.title) { paddingValues ->
      when (val uiState = screenModel.uiState.collectAsState().value) {
        null -> AnalyticsTabViewLoading(
          modifier = Modifier.padding(paddingValues),
        )

        else -> AnalyticsTabViewReady(
          modifier = Modifier.padding(paddingValues),
          uiState = uiState,
          updateSelectedChartType = screenModel::updateSelectedChartType,
        )
      }
    }
  }

  override val options: TabOptions
    @Composable
    get() {
      val title = stringResource(Res.string.analytics_tab_title)
      val icon = rememberVectorPainter(SymbolsRounded.DataUsage)

      return remember(title) { TabOptions(index = 1u, title = title, icon = icon) }
    }
}

@Composable
expect fun Screen.getAnalyticsTabModel(): AnalyticsTabModel

expect class AnalyticsTabModel : ScreenModel {
  val uiState: StateFlow<AnalyticsTabUIState?>
  fun updateSelectedChartType(chartType: ChartType)
}

data class AnalyticsTabUIState(
  val chartDataByCategory: ChartDataByCategory,
  val chartDataByExtension: ChartDataByExtension,
  val selectedChartDataType: ChartType,
  val largestIndexedObject: IndexedObject?,
)

data class ChartDataByCategory(val labels: List<FileCategory>, val data: List<Float>) {
  @Composable
  fun toFormattedString(index: Int): String {
    val label = stringResource(labels[index].res)
    val value = data[index].roundToInt()
    return "$label ($value)"
  }
}

data class ChartDataByExtension(val labels: List<String?>, val data: List<Float>) {
  @Composable
  fun toFormattedString(index: Int): String {
    val label = labels[index] ?: stringResource(Res.string.other)
    val value = data[index].roundToInt()
    return "$label ($value)"
  }
}

enum class ChartType {
  ByCategory,
  ByExt,
}

@Composable
fun AnalyticsTabViewReady(
  modifier: Modifier,
  uiState: AnalyticsTabUIState,
  updateSelectedChartType: (ChartType) -> Unit,
) {
  val scrollState = rememberScrollState()

  RowWithScrollbar(
    modifier = modifier.padding(horizontal = 16.dp),
    scrollState = scrollState,
  ) {
    Column(
      modifier = Modifier.verticalScroll(state = scrollState).weight(1f),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      val shapeMask = RoundedCornerShape(12.dp)
      Chart(
        modifier = Modifier.fillMaxWidth()
          .clip(shapeMask)
          .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shapeMask),
        chartDataByCategory = uiState.chartDataByCategory,
        chartDataByExtension = uiState.chartDataByExtension,
        selectedChartDataType = uiState.selectedChartDataType,
        updateSelectedChartType = updateSelectedChartType,
      )

      if (uiState.largestIndexedObject != null) {
        OutlinedCard(
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            FileThumbnail(
              modifier = Modifier.clip(RoundedCornerShape(8.dp)).size(72.dp),
              item = uiState.largestIndexedObject,
            )
            Column(
              modifier = Modifier.weight(1f)
            ) {
              Text(
                text = stringResource(Res.string.analytics_tab_largest_file),
                style = MaterialTheme.typography.labelMedium,
              )
              Text(uiState.largestIndexedObject.path.name)
              Text(uiState.largestIndexedObject.path.toString())
              Text(formatBytes(uiState.largestIndexedObject.sizeBytes))
            }
          }
        }
      }
    }
  }
}

@Composable
fun AnalyticsTabViewLoading(modifier: Modifier) = Column(
  modifier = modifier.fillMaxSize(),
  verticalArrangement = Arrangement.Center,
  horizontalAlignment = Alignment.CenterHorizontally,
) {
  CircularProgressIndicator()
  Text(stringResource(Res.string.loading))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Chart(
  modifier: Modifier,
  chartDataByCategory: ChartDataByCategory,
  chartDataByExtension: ChartDataByExtension,
  selectedChartDataType: ChartType,
  updateSelectedChartType: (ChartType) -> Unit,
) {
  Column(
    modifier = modifier.padding(12.dp).fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    SingleChoiceSegmentedButtonRow {
      SegmentedButton(
        selected = selectedChartDataType == ChartType.ByCategory,
        shape = SegmentedButtonDefaults.itemShape(0, 2),
        onClick = { updateSelectedChartType(ChartType.ByCategory) },
      ) {
        Text(stringResource(Res.string.file_categories))
      }

      SegmentedButton(
        selected = selectedChartDataType == ChartType.ByExt,
        shape = SegmentedButtonDefaults.itemShape(1, 2),
        onClick = { updateSelectedChartType(ChartType.ByExt) },
      ) {
        Text(stringResource(Res.string.extensions))
      }
    }

    when (selectedChartDataType) {
      ChartType.ByCategory -> {
        val palette = remember(chartDataByCategory.data.size) {
          generateHueColorPalette(chartDataByCategory.data.size)
        }
        BasicChart(
          values = chartDataByCategory.data,
          palette = palette,
          label = { chartDataByCategory.toFormattedString(it) },
        )
        BasicChartLegend(
          data = chartDataByCategory.data,
          palette = palette,
          label = { chartDataByCategory.toFormattedString(it) },
        )
      }

      ChartType.ByExt -> {
        val palette = remember(chartDataByExtension.data.size) {
          generateHueColorPalette(chartDataByExtension.data.size)
        }
        BasicChart(
          values = chartDataByExtension.data,
          palette = palette,
          label = { chartDataByExtension.toFormattedString(it) },
        )
        BasicChartLegend(
          data = chartDataByExtension.data,
          palette = palette,
          label = { chartDataByExtension.toFormattedString(it) },
        )
      }
    }
  }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun BasicChart(
  values: List<Float>,
  palette: List<Color>,
  label: @Composable (index: Int) -> String,
) {
  PieChart(
    modifier = Modifier.size(300.dp),
    values = values,
    holeSize = 0.5f,
    labelConnector = {},
    slice = { index ->
      DefaultSlice(
        color = palette.getOrNull(index) ?: Color.Transparent,
        antiAlias = true,
        hoverElement = { Text(label(index)) },
      )
    },
  )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColumnScope.BasicChartLegend(
  data: List<Float>,
  palette: List<Color>,
  label: @Composable (index: Int) -> String,
) {
  FlowRow(
    modifier = Modifier.align(Alignment.CenterHorizontally),
    verticalArrangement = Arrangement.spacedBy(4.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
  ) {
    data.forEachIndexed { index, _ ->
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Box(
          modifier = Modifier.background(
            // https://github.com/KoalaPlot/koalaplot-core/issues/60
            color = palette.getOrNull(index) ?: Color.Transparent,
            shape = CircleShape,
          )
            .size(16.dp),
        )
        Text(
          text = label(index),
          style = MaterialTheme.typography.bodyMedium,
          maxLines = 2,
        )
      }
    }
  }
}
