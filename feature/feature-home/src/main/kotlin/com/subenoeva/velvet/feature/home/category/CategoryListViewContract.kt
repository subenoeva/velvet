package com.subenoeva.velvet.feature.home.category

object CategoryListViewContract {

    data class State(val title: String = "", val category: String? = null)

    sealed interface Intent {
        data class Init(val category: String, val title: String) : Intent
        data class OnMovieClick(val movieId: Int) : Intent
        data object OnBack : Intent
    }

    sealed interface Event {
        data class NavigateToDetail(val movieId: Int) : Event
        data object NavigateBack : Event
    }
}
