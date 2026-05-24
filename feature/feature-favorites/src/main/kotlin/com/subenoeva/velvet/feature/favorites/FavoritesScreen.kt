package com.subenoeva.velvet.feature.favorites

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.subenoeva.velvet.core.common.presentation.ObserveEvents
import com.subenoeva.velvet.core.ui.component.EmptyState
import com.subenoeva.velvet.core.ui.component.VelvetTopBar
import com.subenoeva.velvet.core.ui.theme.VelvetSurface
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Event.NavigateToDetail
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Intent.OnExitSelectionMode
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Intent.OnMovieClick
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Intent.OnMovieLongPress
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Intent.OnRemoveFavorite
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Intent.OnRemoveSelected
import com.subenoeva.velvet.feature.favorites.component.FavoriteMovieCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isSelectionMode = state.selectedIds.isNotEmpty()

    BackHandler(enabled = isSelectionMode) {
        viewModel.sendIntent(OnExitSelectionMode)
    }

    ObserveEvents(viewModel.events) { event ->
        when (event) {
            is NavigateToDetail -> onNavigateToDetail(event.movieId)
        }
    }

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                TopAppBar(
                    title = { Text("${state.selectedIds.size} seleccionadas") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.sendIntent(OnExitSelectionMode) }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancelar selección")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.sendIntent(OnRemoveSelected) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar seleccionadas")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = VelvetSurface)
                )
            } else {
                VelvetTopBar(title = "Favoritos")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.movies.isEmpty() -> {
                    EmptyState(
                        message = "No tienes favoritos aún",
                        icon = Icons.Default.Favorite,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.movies, key = { it.id }) { movie ->
                            FavoriteMovieCard(
                                movie = movie,
                                isSelectionMode = isSelectionMode,
                                isSelected = state.selectedIds.contains(movie.id),
                                onClick = { viewModel.sendIntent(OnMovieClick(movie.id)) },
                                onLongClick = { viewModel.sendIntent(OnMovieLongPress(movie)) },
                                onRemove = { viewModel.sendIntent(OnRemoveFavorite(movie)) }
                            )
                        }
                    }
                }
            }
        }
    }
}
