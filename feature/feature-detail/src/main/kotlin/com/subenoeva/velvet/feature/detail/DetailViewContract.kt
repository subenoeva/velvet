package com.subenoeva.velvet.feature.detail

import com.subenoeva.velvet.core.domain.model.Cast
import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.domain.model.MovieDetail

object DetailViewContract {

    data class State(
        val movie: MovieDetail? = null,
        val cast: List<Cast> = emptyList(),
        val similarMovies: List<Movie> = emptyList(),
        val isFavorite: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed interface Intent {
        data class LoadDetail(val movieId: Int) : Intent
        data object ToggleFavorite : Intent
        data object PlayTrailer : Intent
        data class SimilarMovieClicked(val movieId: Int) : Intent
    }

    sealed interface Event {
        data class OpenTrailer(val youtubeKey: String) : Event
        data class NavigateToDetail(val movieId: Int) : Event
    }
}
