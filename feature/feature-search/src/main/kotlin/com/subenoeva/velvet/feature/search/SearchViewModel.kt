package com.subenoeva.velvet.feature.search

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.subenoeva.velvet.core.common.dispatcher.DispatcherProvider
import com.subenoeva.velvet.core.common.presentation.BaseViewModel
import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.domain.usecase.movie.SearchMoviesUseCase
import com.subenoeva.velvet.feature.search.SearchViewContract.Event
import com.subenoeva.velvet.feature.search.SearchViewContract.Event.NavigateToDetail
import com.subenoeva.velvet.feature.search.SearchViewContract.Intent
import com.subenoeva.velvet.feature.search.SearchViewContract.Intent.OnMovieClick
import com.subenoeva.velvet.feature.search.SearchViewContract.Intent.UpdateQuery
import com.subenoeva.velvet.feature.search.SearchViewContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMovies: SearchMoviesUseCase,
    private val dispatchers: DispatcherProvider
) : BaseViewModel<State, Intent, Event>(State()) {

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val pagingResults: Flow<PagingData<Movie>> = state
        .map { it.query }
        .distinctUntilChanged()
        .debounce(300)
        .filter { it.length >= 2 }
        .flatMapLatest { query -> searchMovies(query) }
        .cachedIn(viewModelScope)

    override suspend fun handleIntent(intent: Intent) = when (intent) {
        is UpdateQuery -> updateState { copy(query = intent.query) }
        is OnMovieClick -> sendEvent(NavigateToDetail(intent.movieId))
    }
}
