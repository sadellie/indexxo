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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.loading
import indexxo.composeapp.generated.resources.rescan
import io.github.sadellie.indexxo.core.designsystem.LocalWindowSizeClass
import io.github.sadellie.indexxo.core.designsystem.component.ScaffoldWithTopBar
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Menu
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.RestartAlt
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.Search
import io.github.sadellie.indexxo.core.designsystem.icons.rounded.SymbolsRounded
import io.github.sadellie.indexxo.core.designsystem.theme.IndexxoTheme
import io.github.sadellie.indexxo.core.designsystem.theme.isDarkThemeEnabled
import io.github.sadellie.indexxo.feature.actions.ActionsTab
import io.github.sadellie.indexxo.feature.analytics.AnalyticsTab
import io.github.sadellie.indexxo.feature.datechart.DateChartTab
import io.github.sadellie.indexxo.feature.export.ExportTab
import io.github.sadellie.indexxo.feature.home.HomeScreen.topTabs
import io.github.sadellie.indexxo.feature.search.SearchScreen
import io.github.sadellie.indexxo.feature.settings.SettingsScreen
import io.github.sadellie.indexxo.feature.warnings.WarningsTab
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

data object HomeScreen : Screen {
  val topTabs by lazy {
    listOf(
      ActionsTab,
      AnalyticsTab,
      // CompareTab,
      DateChartTab,
      ExportTab,
      WarningsTab,
    )
  }

  @Composable
  override fun Content() {
    // https://github.com/adrielcafe/voyager/issues/429
    val isDarkThemeEnabled = rememberUpdatedState(isDarkThemeEnabled())
    if (isDarkThemeEnabled.value) {
      HomeScreenTabNavigator(true)
    } else {
      HomeScreenTabNavigator(false)
    }
  }
}

@Composable
private fun Screen.HomeScreenTabNavigator(isDark: Boolean) = IndexxoTheme(isDark) {
  TabNavigator(ActionsTab) {
    val containerColor = MaterialTheme.colorScheme.surfaceContainer
    val tabNavigator = LocalTabNavigator.current
    val localParentNavigator = LocalNavigator.currentOrThrow.parent
    val presetName = getHomeScreenModel().presetName.collectAsState().value
      ?: stringResource(Res.string.loading)

    when (LocalWindowSizeClass.current.widthSizeClass) {
      WindowWidthSizeClass.Expanded ->
        ExpandedScreen(
          title = presetName,
          tabs = topTabs,
          containerColor = containerColor,
          onSearchClick = { localParentNavigator?.push(SearchScreen) },
          onRescanClick = { localParentNavigator?.replaceAll(SettingsScreen) },
          currentTab = tabNavigator.current,
          onTabClick = { tabNavigator.current = it },
          content = { CurrentTab() },
        )

      WindowWidthSizeClass.Medium ->
        MediumScreen(
          title = presetName,
          tabs = topTabs,
          containerColor = containerColor,
          onSearchClick = { localParentNavigator?.push(SearchScreen) },
          onRescanClick = { localParentNavigator?.replaceAll(SettingsScreen) },
          currentTab = tabNavigator.current,
          onTabClick = { tabNavigator.current = it },
          content = { CurrentTab() },
        )

      WindowWidthSizeClass.Compact ->
        CompactScreen(
          title = presetName,
          tabs = topTabs,
          containerColor = containerColor,
          onSearchClick = { localParentNavigator?.push(SearchScreen) },
          onRescanClick = { localParentNavigator?.replaceAll(SettingsScreen) },
          currentTab = tabNavigator.current,
          onTabClick = { tabNavigator.current = it },
          content = { CurrentTab() },
        )
    }
  }
}

@Composable
expect fun Screen.getHomeScreenModel(): HomeScreenModel

expect class HomeScreenModel : ScreenModel {
  val presetName: StateFlow<String?>
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedScreen(
  title: String,
  tabs: List<Tab>,
  containerColor: Color,
  onSearchClick: () -> Unit,
  onRescanClick: () -> Unit,
  currentTab: Tab,
  onTabClick: (Tab) -> Unit,
  content: @Composable BoxScope.() -> Unit,
) {
  ScaffoldWithTopBar(
    title = title,
    colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor),
    actions = { IconButton(onClick = onSearchClick) { Icon(SymbolsRounded.Search, null) } },
    containerColor = containerColor,
  ) { paddingValues ->
    PermanentNavigationDrawer(
      modifier = Modifier.padding(paddingValues),
      drawerContent = {
        PermanentDrawerSheet(drawerContainerColor = containerColor) {
          Spacer(Modifier.height(12.dp))
          Column(Modifier.verticalScroll(rememberScrollState()).weight(1f)) {
            tabs.forEach { tab ->
              NavigationDrawerItem(
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                colors =
                  NavigationDrawerItemDefaults.colors(unselectedContainerColor = containerColor),
                selected = currentTab == tab,
                onClick = { onTabClick(tab) },
                icon = {
                  Icon(painter = tab.options.icon!!, contentDescription = tab.options.title)
                },
                label = { Text(tab.options.title) },
              )
            }
          }
          NavigationDrawerItem(
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = containerColor),
            selected = false,
            onClick = onRescanClick,
            icon = {
              Icon(
                painter = rememberVectorPainter(SymbolsRounded.RestartAlt),
                contentDescription = null,
              )
            },
            label = { Text(stringResource(Res.string.rescan)) },
          )
          Spacer(Modifier.height(12.dp))
        }
      },
    ) {
      TabContentInBox(modifier = Modifier.fillMaxSize()) { content() }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediumScreen(
  title: String,
  tabs: List<Tab>,
  containerColor: Color,
  onSearchClick: () -> Unit,
  onRescanClick: () -> Unit,
  currentTab: Tab,
  onTabClick: (Tab) -> Unit,
  content: @Composable BoxScope.() -> Unit,
) {
  ScaffoldWithTopBar(
    title = title,
    colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor),
    actions = { IconButton(onClick = onSearchClick) { Icon(SymbolsRounded.Search, null) } },
    containerColor = containerColor,
  ) { paddingValues ->
    PermanentNavigationDrawer(
      modifier = Modifier.padding(paddingValues),
      drawerContent = {
        NavigationRail(containerColor = containerColor) {
          Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            tabs.forEach { tab ->
              NavigationRailItem(
                selected = currentTab == tab,
                onClick = { onTabClick(tab) },
                icon = {
                  Icon(painter = tab.options.icon!!, contentDescription = tab.options.title)
                },
                label = { Text(tab.options.title) },
              )
            }
          }

          NavigationRailItem(
            selected = false,
            onClick = onRescanClick,
            icon = {
              Icon(
                painter = rememberVectorPainter(SymbolsRounded.RestartAlt),
                contentDescription = null,
              )
            },
            label = { Text(stringResource(Res.string.rescan)) },
          )

          Spacer(Modifier.height(12.dp))
        }
      },
    ) {
      TabContentInBox(modifier = Modifier.fillMaxSize()) { content() }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactScreen(
  title: String,
  tabs: List<Tab>,
  containerColor: Color,
  onSearchClick: () -> Unit,
  onRescanClick: () -> Unit,
  currentTab: Tab,
  onTabClick: (Tab) -> Unit,
  content: @Composable BoxScope.() -> Unit,
) {
  val drawerState = rememberDrawerState(DrawerValue.Closed)
  val coroutine = rememberCoroutineScope()

  ModalNavigationDrawer(
    drawerContent = {
      ModalDrawerSheet {
        Spacer(Modifier.height(12.dp))
        Column(Modifier.verticalScroll(rememberScrollState()).weight(1f)) {
          tabs.forEach { tab ->
            NavigationDrawerItem(
              modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
              selected = currentTab == tab,
              onClick = {
                coroutine.launch { drawerState.close() }
                onTabClick(tab)
              },
              icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) },
              label = { Text(tab.options.title) },
            )
          }
        }
        NavigationDrawerItem(
          modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
          selected = false,
          onClick = onRescanClick,
          icon = {
            Icon(
              painter = rememberVectorPainter(SymbolsRounded.RestartAlt),
              contentDescription = null,
            )
          },
          label = { Text(stringResource(Res.string.rescan)) },
        )
        Spacer(Modifier.height(12.dp))
      }
    },
    drawerState = drawerState,
  ) {
    ScaffoldWithTopBar(
      title = title,
      colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor),
      navigationIcon = {
        IconButton(onClick = { coroutine.launch { drawerState.open() } }) {
          Icon(SymbolsRounded.Menu, null)
        }
      },
      actions = { IconButton(onClick = onSearchClick) { Icon(SymbolsRounded.Search, null) } },
    ) { paddingValues ->
      Box(modifier = Modifier.padding(paddingValues)) { content() }
    }
  }
}

@Composable
private fun TabContentInBox(
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.() -> Unit,
) {
  Box(
    modifier =
      modifier
        .padding(end = 16.dp, bottom = 16.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(color = MaterialTheme.colorScheme.surfaceContainerLowest)
  ) {
    content()
  }
}
