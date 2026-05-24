package com.subenoeva.velvet.feature.favorites

import androidx.lifecycle.viewModelScope
import com.subenoeva.velvet.core.common.presentation.BaseViewModel
import com.subenoeva.velvet.core.domain.usecase.NoParams
import com.subenoeva.velvet.core.domain.usecase.favorite.GetFavoritesUseCase
import com.subenoeva.velvet.core.domain.usecase.favorite.ToggleFavoriteUseCase
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Event
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Event.NavigateToDetail
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Intent
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Intent.OnExitSelectionMode
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Intent.OnMovieClick
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Intent.OnMovieLongPress
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Intent.OnRemoveFavorite
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Intent.OnRemoveSelected
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Intent.OnToggleSelection
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavorites: GetFavoritesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase
) : BaseViewModel<State, Intent, Event>(State()) {

    init {
        getFavorites(NoParams)
            .onEach { movies -> updateState { copy(movies = movies, isLoading = false) } }
            .launchIn(viewModelScope)
    }

    override suspend fun handleIntent(intent: Intent) = when (intent) {
        is OnMovieClick -> {
            if (state.value.selectedIds.isEmpty()) {
                sendEvent(NavigateToDetail(intent.movieId))
            } else {
                updateState { copy(selectedIds = selectedIds.toggle(intent.movieId)) }
            }
        }
        is OnMovieLongPress    -> updateState { copy(selectedIds = setOf(intent.movie.id)) }
        is OnToggleSelection   -> updateState { copy(selectedIds = selectedIds.toggle(intent.movie.id)) }
        is OnRemoveFavorite    -> toggleFavorite(intent.movie)
        is OnRemoveSelected    -> {
            state.value.selectedIds.forEach { id ->
                state.value.movies.find { it.id == id }?.let { toggleFavorite(it) }
            }
            updateState { copy(selectedIds = emptySet()) }
        }
        is OnExitSelectionMode -> updateState { copy(selectedIds = emptySet()) }
    }
}

private fun Set<Int>.toggle(id: Int): Set<Int> = if (contains(id)) this - id else this + id
