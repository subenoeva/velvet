package com.subenoeva.velvet.feature.search

object SearchViewContract {

    data class State(
        val query: String = ""
    )

    sealed interface Intent {
        data class UpdateQuery(val query: String) : Intent
        data class OnMovieClick(val movieId: Int) : Intent
    }

    sealed interface Event {
        data class NavigateToDetail(val movieId: Int) : Event
    }
}
