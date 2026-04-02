package com.subenoeva.velvet.feature.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.subenoeva.velvet.core.common.presentation.ObserveEvents
import com.subenoeva.velvet.core.ui.component.ErrorState
import com.subenoeva.velvet.feature.home.HomeViewContract.Event
import com.subenoeva.velvet.feature.home.HomeViewContract.Intent
import com.subenoeva.velvet.feature.home.HomeViewContract.State
import com.subenoeva.velvet.feature.home.component.MovieRow
import com.subenoeva.velvet.feature.home.component.TrendingCarousel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToDetail: (Int) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEvents(viewModel.events) { event ->
        when (event) {
            is Event.NavigateToDetail -> onNavigateToDetail(event.movieId)
        }
    }

    HomeScreenContent(
        state = state,
        onMovieClick = { viewModel.sendIntent(Intent.OnMovieClick(it)) },
        onRetry = { viewModel.sendIntent(Intent.Refresh) }
    )
}

@Composable
private fun HomeScreenContent(
    state: State,
    onMovieClick: (Int) -> Unit,
    onRetry: () -> Unit
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
                item { MovieRow(title = "Populares", movies = state.popular, onMovieClick = onMovieClick) }
            }
            if (state.topRated.isNotEmpty()) {
                item { MovieRow(title = "Mejor valoradas", movies = state.topRated, onMovieClick = onMovieClick) }
            }
            if (state.upcoming.isNotEmpty()) {
                item { MovieRow(title = "Próximos estrenos", movies = state.upcoming, onMovieClick = onMovieClick) }
            }
        }
    }
}

@Composable
private fun HomeShimmer() {
    // Placeholder shown while initial load is in progress
    // Uses LoadingShimmer from core-ui — implement as a LazyColumn with shimmer items
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            com.subenoeva.velvet.core.ui.component.LoadingShimmer(
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}
