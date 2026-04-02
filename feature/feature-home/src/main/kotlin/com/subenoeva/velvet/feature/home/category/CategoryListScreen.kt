package com.subenoeva.velvet.feature.home.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.subenoeva.velvet.core.common.presentation.ObserveEvents
import com.subenoeva.velvet.core.ui.component.ErrorState
import com.subenoeva.velvet.core.ui.component.MovieCard
import com.subenoeva.velvet.feature.home.category.CategoryListViewContract.Event.NavigateBack
import com.subenoeva.velvet.feature.home.category.CategoryListViewContract.Event.NavigateToDetail
import com.subenoeva.velvet.feature.home.category.CategoryListViewContract.Intent.Init
import com.subenoeva.velvet.feature.home.category.CategoryListViewContract.Intent.OnBack
import com.subenoeva.velvet.feature.home.category.CategoryListViewContract.Intent.OnMovieClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    category: String,
    title: String,
    viewModel: CategoryListViewModel = hiltViewModel(),
    onNavigateToDetail: (Int) -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(category) { viewModel.sendIntent(Init(category, title)) }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val movies = viewModel.pagingFlow.collectAsLazyPagingItems()

    ObserveEvents(viewModel.events) { event ->
        when (event) {
            is NavigateToDetail -> onNavigateToDetail(event.movieId)
            NavigateBack -> onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.title) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.sendIntent(OnBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val refresh = movies.loadState.refresh) {
            is LoadState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            is LoadState.Error -> {
                ErrorState(
                    message = refresh.error.localizedMessage ?: "Error desconocido",
                    onRetry = { movies.retry() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(
                        start = 8.dp,
                        end = 8.dp,
                        top = innerPadding.calculateTopPadding() + 8.dp,
                        bottom = 8.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(movies.itemCount) { index ->
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
                }
            }
        }
    }
}
