package com.subenoeva.velvet.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.subenoeva.velvet.core.ui.theme.VelvetAccent
import com.subenoeva.velvet.core.ui.theme.VelvetSurface
import com.subenoeva.velvet.core.ui.theme.VelvetTextSecondary
import com.subenoeva.velvet.feature.favorites.FavoritesRoute
import com.subenoeva.velvet.feature.home.HomeRoute
import com.subenoeva.velvet.feature.search.SearchRoute
import com.subenoeva.velvet.feature.settings.SettingsRoute

private data class TabItem(
    val route: NavKey,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val isSelected: (NavKey?) -> Boolean,
)

private val tabs = listOf(
    TabItem(
        route = HomeRoute,
        label = "Inicio",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        isSelected = { it is HomeRoute }
    ),
    TabItem(
        route = SearchRoute,
        label = "Buscar",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Filled.Search,
        isSelected = { it is SearchRoute }
    ),
    TabItem(
        route = FavoritesRoute,
        label = "Favoritos",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Filled.FavoriteBorder,
        isSelected = { it is FavoritesRoute }
    ),
    TabItem(
        route = SettingsRoute,
        label = "Ajustes",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        isSelected = { it is SettingsRoute }
    ),
)

@Composable
fun VelvetBottomNavBar(backStack: NavBackStack<NavKey>) {
    val currentRoute = backStack.lastOrNull()
    NavigationBar(containerColor = VelvetSurface) {
        tabs.forEach { tab ->
            val selected = tab.isSelected(currentRoute)
            NavigationBarItem(
                selected = selected,
                onClick = { navigateToTab(backStack, tab.route, tab.isSelected) },
                icon = {
                    Icon(
                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.label
                    )
                },
                label = { Text(tab.label, fontWeight = if (selected) Bold else Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = VelvetAccent,
                    selectedTextColor = VelvetAccent,
                    unselectedIconColor = VelvetTextSecondary,
                    unselectedTextColor = VelvetTextSecondary,
                    indicatorColor = VelvetSurface,
                )
            )
        }
    }
}

private fun navigateToTab(
    backStack: NavBackStack<NavKey>,
    route: NavKey,
    isTarget: (NavKey?) -> Boolean,
) {
    val existingIndex = backStack.indexOfLast { isTarget(it) }
    if (existingIndex >= 0) {
        while (backStack.size > existingIndex + 1) {
            backStack.removeLastOrNull()
        }
    } else {
        backStack.add(route)
    }
}
