package com.subenoeva.velvet.core.domain.usecase.favorite

import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.domain.repository.MovieRepository
import com.subenoeva.velvet.core.domain.usecase.UseCase
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: MovieRepository
) : UseCase<Movie, Unit>() {
    override suspend fun execute(params: Movie) = repository.toggleFavorite(params)
}
