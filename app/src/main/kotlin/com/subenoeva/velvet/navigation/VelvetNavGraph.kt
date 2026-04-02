package com.subenoeva.velvet.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.subenoeva.velvet.feature.detail.DetailRoute
import com.subenoeva.velvet.feature.detail.DetailScreen
import com.subenoeva.velvet.feature.favorites.FavoritesRoute
import com.subenoeva.velvet.feature.favorites.FavoritesScreen
import com.subenoeva.velvet.feature.home.category.CategoryListScreen
import com.subenoeva.velvet.feature.home.category.CategoryRoute
import com.subenoeva.velvet.feature.home.HomeRoute
import com.subenoeva.velvet.feature.home.HomeScreen
import com.subenoeva.velvet.feature.search.SearchRoute
import com.subenoeva.velvet.feature.search.SearchScreen
import com.subenoeva.velvet.feature.settings.SettingsRoute
import com.subenoeva.velvet.feature.settings.SettingsScreen

@Composable
fun VelvetNavGraph() {
    val backStack = rememberNavBackStack(HomeRoute)
    Scaffold(
        bottomBar = { VelvetBottomNavBar(backStack) }
    ) { padding ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(padding),
            entryProvider = entryProvider {
                entry<HomeRoute> {
                    HomeScreen(
                        onNavigateToDetail = { backStack.add(DetailRoute(it)) },
                        onNavigateToCategoryList = { category, title ->
                            backStack.add(CategoryRoute(category, title))
                        }
                    )
                }
                entry<CategoryRoute> { route ->
                    CategoryListScreen(
                        category = route.category,
                        title = route.title,
                        onNavigateToDetail = { backStack.add(DetailRoute(it)) },
                        onBack = { backStack.removeLastOrNull() }
                    )
                }
                entry<SearchRoute> {
                    SearchScreen(
                        onNavigateToDetail = { backStack.add(DetailRoute(it)) }
                    )
                }
                entry<DetailRoute> { detailRoute ->
                    DetailScreen(
                        movieId = detailRoute.movieId,
                        onBack = { backStack.removeLastOrNull() },
                        onNavigateToDetail = { backStack.add(DetailRoute(it)) }
                    )
                }
                entry<FavoritesRoute> {
                    FavoritesScreen(
                        onNavigateToDetail = { backStack.add(DetailRoute(it)) }
                    )
                }
                entry<SettingsRoute> {
                    SettingsScreen()
                }
            }
        )
    }
}
