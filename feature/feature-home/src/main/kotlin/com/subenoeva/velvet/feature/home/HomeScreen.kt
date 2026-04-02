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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.subenoeva.velvet.core.common.presentation.ObserveEvents
import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.ui.component.MovieCard
import com.subenoeva.velvet.feature.home.HomeViewContract.Event.NavigateToDetail
import com.subenoeva.velvet.feature.home.HomeViewContract.Intent.OnMovieClick

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToDetail: (Int) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val movies = state.moviesPagingFlow.collectAsLazyPagingItems()

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
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(140.dp), // Se adapta al tamaño del MovieCard
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        if (movies.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator()
        }
    }
}
