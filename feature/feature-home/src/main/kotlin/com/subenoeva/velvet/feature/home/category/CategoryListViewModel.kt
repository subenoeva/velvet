package com.subenoeva.velvet.feature.home.category

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.subenoeva.velvet.core.common.presentation.BaseViewModel
import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.domain.usecase.NoParams
import com.subenoeva.velvet.core.domain.usecase.movie.GetPopularMoviesUseCase
import com.subenoeva.velvet.core.domain.usecase.movie.GetTopRatedMoviesUseCase
import com.subenoeva.velvet.core.domain.usecase.movie.GetUpcomingMoviesUseCase
import com.subenoeva.velvet.feature.home.category.CategoryListViewContract.Event
import com.subenoeva.velvet.feature.home.category.CategoryListViewContract.Event.NavigateBack
import com.subenoeva.velvet.feature.home.category.CategoryListViewContract.Event.NavigateToDetail
import com.subenoeva.velvet.feature.home.category.CategoryListViewContract.Intent
import com.subenoeva.velvet.feature.home.category.CategoryListViewContract.Intent.Init
import com.subenoeva.velvet.feature.home.category.CategoryListViewContract.Intent.OnBack
import com.subenoeva.velvet.feature.home.category.CategoryListViewContract.Intent.OnMovieClick
import com.subenoeva.velvet.feature.home.category.CategoryListViewContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val getPopular: GetPopularMoviesUseCase,
    private val getTopRated: GetTopRatedMoviesUseCase,
    private val getUpcoming: GetUpcomingMoviesUseCase,
) : BaseViewModel<State, Intent, Event>(State()) {

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingFlow: Flow<PagingData<Movie>> = state
        .map { it.category }
        .filterNotNull()
        .distinctUntilChanged()
        .flatMapLatest { category ->
            when (category) {
                "top_rated" -> getTopRated(NoParams)
                "upcoming" -> getUpcoming(NoParams)
                else -> getPopular(NoParams)
            }
        }
        .cachedIn(viewModelScope)

    override suspend fun handleIntent(intent: Intent) = when (intent) {
        is Init -> updateState { copy(category = intent.category, title = intent.title) }

        is OnMovieClick -> sendEvent(NavigateToDetail(intent.movieId))
        OnBack -> sendEvent(NavigateBack)
    }
}
