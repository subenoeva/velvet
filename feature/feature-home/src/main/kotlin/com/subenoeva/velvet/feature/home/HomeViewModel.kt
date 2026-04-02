package com.subenoeva.velvet.feature.home

import androidx.lifecycle.viewModelScope
import com.subenoeva.velvet.core.common.dispatcher.DispatcherProvider
import com.subenoeva.velvet.core.common.presentation.BaseViewModel
import com.subenoeva.velvet.core.common.result.Result
import com.subenoeva.velvet.core.domain.usecase.NoParams
import com.subenoeva.velvet.core.domain.usecase.movie.GetPopularPreviewUseCase
import com.subenoeva.velvet.core.domain.usecase.movie.GetTopRatedPreviewUseCase
import com.subenoeva.velvet.core.domain.usecase.movie.GetTrendingMoviesUseCase
import com.subenoeva.velvet.core.domain.usecase.movie.GetUpcomingPreviewUseCase
import com.subenoeva.velvet.feature.home.HomeViewContract.Event
import com.subenoeva.velvet.feature.home.HomeViewContract.Event.NavigateToDetail
import com.subenoeva.velvet.feature.home.HomeViewContract.Intent
import com.subenoeva.velvet.feature.home.HomeViewContract.Intent.OnMovieClick
import com.subenoeva.velvet.feature.home.HomeViewContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTrending: GetTrendingMoviesUseCase,
    private val getPopularPreview: GetPopularPreviewUseCase,
    private val getTopRatedPreview: GetTopRatedPreviewUseCase,
    private val getUpcomingPreview: GetUpcomingPreviewUseCase,
    private val dispatchers: DispatcherProvider
) : BaseViewModel<State, Intent, Event>(State()) {

    init { sendIntent(Intent.LoadContent) }

    override suspend fun handleIntent(intent: Intent) = when (intent) {
        Intent.LoadContent, Intent.Refresh -> loadContent()
        is OnMovieClick -> sendEvent(NavigateToDetail(intent.movieId))
    }

    private fun loadContent() {
        viewModelScope.launch(dispatchers.io) {
            updateState { copy(isLoading = true, error = null) }
            combine(
                getTrending(NoParams),
                getPopularPreview(NoParams),
                getTopRatedPreview(NoParams),
                getUpcomingPreview(NoParams)
            ) { trending, popular, topRated, upcoming ->
                currentState.copy(
                    trending = (trending as? Result.Success)?.data ?: currentState.trending,
                    popular  = (popular  as? Result.Success)?.data ?: currentState.popular,
                    topRated = (topRated as? Result.Success)?.data ?: currentState.topRated,
                    upcoming = (upcoming as? Result.Success)?.data ?: currentState.upcoming,
                    isLoading = false,
                    error = if (trending is Result.Error) trending.message else null
                )
            }.collect { setState(it) }
        }
    }
}
