package com.subenoeva.velvet.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.subenoeva.velvet.core.common.presentation.ObserveEvents
import com.subenoeva.velvet.core.ui.component.ErrorState
import com.subenoeva.velvet.core.ui.component.LoadingShimmer
import com.subenoeva.velvet.core.ui.component.ShimmerMovieCard
import com.subenoeva.velvet.feature.home.HomeViewContract.Event.NavigateToCategoryList
import com.subenoeva.velvet.feature.home.HomeViewContract.Event.NavigateToDetail
import com.subenoeva.velvet.feature.home.HomeViewContract.Intent.OnMovieClick
import com.subenoeva.velvet.feature.home.HomeViewContract.Intent.OnSeeAllClick
import com.subenoeva.velvet.feature.home.HomeViewContract.Intent.Refresh
import com.subenoeva.velvet.feature.home.HomeViewContract.State
import com.subenoeva.velvet.feature.home.component.MovieRow
import com.subenoeva.velvet.feature.home.component.TrendingCarousel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToCategoryList: (category: String, title: String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEvents(viewModel.events) { event ->
        when (event) {
            is NavigateToDetail -> onNavigateToDetail(event.movieId)
            is NavigateToCategoryList -> onNavigateToCategoryList(event.category, event.title)
        }
    }

    HomeScreenContent(
        state = state,
        onMovieClick = { viewModel.sendIntent(OnMovieClick(it)) },
        onRetry = { viewModel.sendIntent(Refresh) },
        onSeeAll = { category, title -> viewModel.sendIntent(OnSeeAllClick(category, title)) }
    )
}

@Composable
private fun HomeScreenContent(
    state: State,
    onMovieClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onSeeAll: (category: String, title: String) -> Unit
) {
    when {
        state.isLoading && state.trending.isEmpty() -> HomeShimmer()
        state.error != null && state.trending.isEmpty() -> ErrorState(
            message = state.error,
            onRetry = onRetry,
            modifier = Modifier.fillMaxSize()
        )

        else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (state.trending.isNotEmpty()) {
                item { TrendingCarousel(movies = state.trending, onMovieClick = onMovieClick) }
            }
            if (state.popular.isNotEmpty()) {
                item {
                    MovieRow(
                        title = "Populares",
                        movies = state.popular,
                        onMovieClick = onMovieClick,
                        onSeeAll = { onSeeAll("popular", "Populares") }
                    )
                }
            }
            if (state.topRated.isNotEmpty()) {
                item {
                    MovieRow(
                        title = "Mejor valoradas",
                        movies = state.topRated,
                        onMovieClick = onMovieClick,
                        onSeeAll = { onSeeAll("top_rated", "Mejor valoradas") }
                    )
                }
            }
            if (state.upcoming.isNotEmpty()) {
                item {
                    MovieRow(
                        title = "Próximos estrenos",
                        movies = state.upcoming,
                        onMovieClick = onMovieClick,
                        onSeeAll = { onSeeAll("upcoming", "Próximos estrenos") }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeShimmer() {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(3) {
                    LoadingShimmer(
                        modifier = Modifier
                            .width(300.dp)
                            .height(180.dp)
                    )
                }
            }
        }
        items(2) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                LoadingShimmer(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .width(140.dp)
                        .height(18.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(4) { ShimmerMovieCard() }
                }
            }
        }
    }
}
