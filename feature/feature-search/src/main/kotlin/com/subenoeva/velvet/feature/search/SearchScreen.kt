package com.subenoeva.velvet.feature.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.subenoeva.velvet.core.common.presentation.ObserveEvents
import com.subenoeva.velvet.core.ui.component.ErrorState
import com.subenoeva.velvet.core.ui.component.MovieCard
import com.subenoeva.velvet.feature.search.SearchViewContract.Event.NavigateToDetail
import com.subenoeva.velvet.feature.search.SearchViewContract.Intent.OnMovieClick
import com.subenoeva.velvet.feature.search.SearchViewContract.Intent.UpdateQuery

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val movies = viewModel.pagingResults.collectAsLazyPagingItems()

    ObserveEvents(viewModel.events) { event ->
        when (event) {
            is NavigateToDetail -> onNavigateToDetail(event.movieId)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        DockedSearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = state.query,
                    onQueryChange = { viewModel.sendIntent(UpdateQuery(it)) },
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {}
                )
            },
            expanded = false,
            onExpandedChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {}

        if (state.query.length < 2) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Escribe al menos 2 caracteres")
            }
        } else {
            when (val refresh = movies.loadState.refresh) {
                is LoadState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is LoadState.Error -> {
                    ErrorState(
                        message = refresh.error.localizedMessage ?: "Error desconocido",
                        onRetry = { movies.retry() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    if (movies.itemCount == 0 && movies.loadState.append.endOfPaginationReached) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No se encontraron resultados para \"${state.query}\"")
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                count = movies.itemCount,
                                key = movies.itemKey { it.id }
                            ) { index ->
                                val movie = movies[index]
                                if (movie != null) {
                                    MovieCard(
                                        posterPath = movie.posterPath,
                                        title = movie.title,
                                        rating = movie.voteAverage,
                                        onClick = { viewModel.sendIntent(OnMovieClick(movie.id)) }
                                    )
                                }
                            }
                            if (movies.loadState.append is LoadState.Loading) {
                                item {
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
                            val appendError = movies.loadState.append as? LoadState.Error
                            if (appendError != null) {
                                item {
                                    ErrorState(
                                        message = appendError.error.localizedMessage ?: "Error al cargar más",
                                        onRetry = { movies.retry() },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
