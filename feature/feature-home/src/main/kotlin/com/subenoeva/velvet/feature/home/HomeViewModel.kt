package com.subenoeva.velvet.feature.home

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.subenoeva.velvet.core.common.presentation.BaseViewModel
import com.subenoeva.velvet.core.domain.usecase.NoParams
import com.subenoeva.velvet.core.domain.usecase.movie.GetPopularMoviesUseCase
import com.subenoeva.velvet.feature.home.HomeViewContract.Event
import com.subenoeva.velvet.feature.home.HomeViewContract.Event.NavigateToDetail
import com.subenoeva.velvet.feature.home.HomeViewContract.Intent
import com.subenoeva.velvet.feature.home.HomeViewContract.Intent.OnMovieClick
import com.subenoeva.velvet.feature.home.HomeViewContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getPopularMoviesUseCase: GetPopularMoviesUseCase
) : BaseViewModel<State, Intent, Event>(State) {

    val moviesPagingFlow = getPopularMoviesUseCase(NoParams).cachedIn(viewModelScope)

    override suspend fun handleIntent(intent: Intent) {
        when (intent) {
            is OnMovieClick -> sendEvent(NavigateToDetail(intent.movieId))
        }
    }
}
