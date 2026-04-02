package com.subenoeva.velvet.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.subenoeva.velvet.core.common.presentation.ObserveEvents
import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.ui.component.ErrorState
import com.subenoeva.velvet.core.ui.component.MovieCard
import com.subenoeva.velvet.core.ui.component.ShimmerMovieCard
import com.subenoeva.velvet.feature.home.HomeViewContract.Event.NavigateToDetail
import com.subenoeva.velvet.feature.home.HomeViewContract.Intent.OnMovieClick

private val GRID_SPACING = 16.dp
private val CARD_MIN_SIZE = 140.dp

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToDetail: (Int) -> Unit
) {
    val movies = viewModel.moviesPagingFlow.collectAsLazyPagingItems()

    ObserveEvents(viewModel.events) { event ->
        when (event) {
            is NavigateToDetail -> onNavigateToDetail(event.movieId)
        }
    }

    HomeScreenContent(
        movies = movies,
        onMovieClick = { movieId -> viewModel.sendIntent(OnMovieClick(movieId)) }
    )
}

@Composable
private fun HomeScreenContent(
    movies: LazyPagingItems<Movie>,
    onMovieClick: (Int) -> Unit
) {
    when (val refreshState = movies.loadState.refresh) {
        is LoadState.Loading -> ShimmerGrid()
        is LoadState.Error -> ErrorState(
            message = refreshState.error.localizedMessage ?: "Error al cargar películas",
            onRetry = { movies.retry() },
            modifier = Modifier.fillMaxSize()
        )

        else -> MovieGrid(movies = movies, onMovieClick = onMovieClick)
    }
}

@Composable
private fun ShimmerGrid() {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = CARD_MIN_SIZE),
        contentPadding = PaddingValues(GRID_SPACING),
        horizontalArrangement = Arrangement.spacedBy(GRID_SPACING),
        verticalArrangement = Arrangement.spacedBy(GRID_SPACING)
    ) {
        items(6) { ShimmerMovieCard(modifier = Modifier.fillMaxWidth()) }
    }
}

@Composable
private fun MovieGrid(
    movies: LazyPagingItems<Movie>,
    onMovieClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = CARD_MIN_SIZE),
        contentPadding = PaddingValues(GRID_SPACING),
        horizontalArrangement = Arrangement.spacedBy(GRID_SPACING),
        verticalArrangement = Arrangement.spacedBy(GRID_SPACING)
    ) {
        items(
            count = movies.itemCount,
            key = movies.itemKey { it.id },
            contentType = movies.itemContentType { "movie" }
        ) { index ->
            val movie = movies[index]
            if (movie != null) {
                MovieCard(
                    posterPath = movie.posterPath,
                    title = movie.title,
                    rating = movie.voteAverage,
                    onClick = { onMovieClick(movie.id) }
                )
            }
        }

        if (movies.loadState.append is LoadState.Loading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(GRID_SPACING),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
