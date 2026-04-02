package com.subenoeva.velvet.feature.home

object HomeViewContract {

    data object State

    sealed interface Intent {
        data class OnMovieClick(val movieId: Int) : Intent
    }

    sealed interface Event {
        data class NavigateToDetail(val movieId: Int) : Event
    }
}
