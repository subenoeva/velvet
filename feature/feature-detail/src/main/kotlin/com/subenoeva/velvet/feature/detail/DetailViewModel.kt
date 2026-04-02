package com.subenoeva.velvet.feature.detail

import androidx.lifecycle.viewModelScope
import com.subenoeva.velvet.core.common.dispatcher.DispatcherProvider
import com.subenoeva.velvet.core.common.presentation.BaseViewModel
import com.subenoeva.velvet.core.common.result.Result
import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.domain.usecase.favorite.IsFavoriteUseCase
import com.subenoeva.velvet.core.domain.usecase.favorite.ToggleFavoriteUseCase
import com.subenoeva.velvet.core.domain.usecase.movie.GetMovieCastUseCase
import com.subenoeva.velvet.core.domain.usecase.movie.GetMovieDetailUseCase
import com.subenoeva.velvet.core.domain.usecase.movie.GetSimilarMoviesUseCase
import com.subenoeva.velvet.feature.detail.DetailViewContract.Event
import com.subenoeva.velvet.feature.detail.DetailViewContract.Event.NavigateToDetail
import com.subenoeva.velvet.feature.detail.DetailViewContract.Event.OpenTrailer
import com.subenoeva.velvet.feature.detail.DetailViewContract.Intent
import com.subenoeva.velvet.feature.detail.DetailViewContract.Intent.LoadDetail
import com.subenoeva.velvet.feature.detail.DetailViewContract.Intent.SimilarMovieClicked
import com.subenoeva.velvet.feature.detail.DetailViewContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getMovieDetail: GetMovieDetailUseCase,
    private val getMovieCast: GetMovieCastUseCase,
    private val getSimilarMovies: GetSimilarMoviesUseCase,
    private val isFavorite: IsFavoriteUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val dispatchers: DispatcherProvider
) : BaseViewModel<State, Intent, Event>(State()) {

    // No init block: movieId is not available at construction time.
    // The screen sends LoadDetail(movieId) via LaunchedEffect after composition.
    override suspend fun handleIntent(intent: Intent) = when (intent) {
        is LoadDetail -> loadDetail(intent.movieId)
        Intent.ToggleFavorite -> toggleFavorite()
        Intent.PlayTrailer -> playTrailer()
        is SimilarMovieClicked -> sendEvent(NavigateToDetail(intent.movieId))
    }

    private var detailJob: Job? = null

    private fun loadDetail(movieId: Int) {
        detailJob?.cancel()
        detailJob = viewModelScope.launch(dispatchers.io) {
            updateState { copy(isLoading = true, error = null) }
            val isFav = isFavorite(movieId)
            updateState { copy(isFavorite = isFav) }
            combine(
                getMovieDetail(movieId),
                getMovieCast(movieId),
                getSimilarMovies(movieId)
            ) { detail, cast, similar ->
                currentState.copy(
                    movie = (detail as? Result.Success)?.data ?: currentState.movie,
                    cast = (cast as? Result.Success)?.data ?: currentState.cast,
                    similarMovies = (similar as? Result.Success)?.data ?: currentState.similarMovies,
                    isLoading = detail is Result.Loading || cast is Result.Loading || similar is Result.Loading,
                    error = when {
                        detail is Result.Error -> detail.message
                        cast is Result.Error -> cast.message
                        similar is Result.Error -> similar.message
                        else -> null
                    }
                )
            }.collect { setState(it) }
        }
    }

    private fun toggleFavorite() {
        val movie = currentState.movie ?: return
        val movieAsMovie = Movie(
            id = movie.id,
            title = movie.title,
            overview = movie.overview,
            posterPath = movie.posterPath,
            backdropPath = movie.backdropPath,
            voteAverage = movie.voteAverage,
            releaseDate = movie.releaseDate,
            genreIds = movie.genres.map { it.id }
        )
        viewModelScope.launch(dispatchers.io) {
            toggleFavoriteUseCase(movieAsMovie)
            val newIsFavorite = isFavorite(movie.id)
            updateState { copy(isFavorite = newIsFavorite) }
        }
    }

    private fun playTrailer() {
        val key = currentState.movie?.videos?.firstOrNull { it.isYouTubeTrailer }?.key
        if (key != null) sendEvent(OpenTrailer(key))
    }
}
