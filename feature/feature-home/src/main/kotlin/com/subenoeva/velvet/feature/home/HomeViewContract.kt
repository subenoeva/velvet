package com.subenoeva.velvet.feature.home

import androidx.paging.PagingData
import com.subenoeva.velvet.core.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class HomeViewContract {

    data class State(
        val isLoading: Boolean = false,
        val moviesPagingFlow: Flow<PagingData<Movie>> = emptyFlow()
    )

    sealed interface Intent {
        data class OnMovieClick(val movieId: Int) : Intent
    }

    sealed interface Event {
        data class NavigateToDetail(val movieId: Int) : Event
    }

}