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

package io.github.sadellie.indexxo.feature.datechart

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.date_chart_tab_title
import io.github.sadellie.indexxo.core.designsystem.component.InDevelopmentScreen
import io.github.sadellie.indexxo.core.designsystem.component.ScaffoldWithTopBar
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.BarChart
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded
import org.jetbrains.compose.resources.stringResource

data object DateChartTab : Tab {
  override val options: TabOptions
    @Composable
    get() {
      val title = stringResource(Res.string.date_chart_tab_title)
      val icon = rememberVectorPainter(SymbolsRounded.BarChart)

      return remember(title) { TabOptions(index = 3u, title = title, icon = icon) }
    }

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    ScaffoldWithTopBar(title = options.title) { paddingValues ->
      InDevelopmentScreen(modifier = Modifier.padding(paddingValues))
    }
  }
}
