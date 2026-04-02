package com.subenoeva.velvet.feature.home

import com.subenoeva.velvet.core.domain.model.Movie

object HomeViewContract {

    data class State(
        val trending: List<Movie> = emptyList(),
        val popular: List<Movie> = emptyList(),
        val topRated: List<Movie> = emptyList(),
        val upcoming: List<Movie> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed interface Intent {
        data object LoadContent : Intent
        data object Refresh : Intent
        data class OnMovieClick(val movieId: Int) : Intent
    }

    sealed interface Event {
        data class NavigateToDetail(val movieId: Int) : Event
    }
}
