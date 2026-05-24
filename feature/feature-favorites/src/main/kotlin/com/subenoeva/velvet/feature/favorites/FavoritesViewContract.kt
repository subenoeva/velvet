package com.subenoeva.velvet.feature.favorites

import com.subenoeva.velvet.core.domain.model.Movie

object FavoritesViewContract {

    data class State(
        val movies: List<Movie> = emptyList(),
        val isLoading: Boolean = true,
        val selectedIds: Set<Int> = emptySet()
    )

    sealed interface Intent {
        data class OnMovieClick(val movieId: Int) : Intent
        data class OnMovieLongPress(val movie: Movie) : Intent
        data class OnToggleSelection(val movie: Movie) : Intent
        data class OnRemoveFavorite(val movie: Movie) : Intent
        data object OnRemoveSelected : Intent
        data object OnExitSelectionMode : Intent
    }

    sealed interface Event {
        data class NavigateToDetail(val movieId: Int) : Event
    }
}
