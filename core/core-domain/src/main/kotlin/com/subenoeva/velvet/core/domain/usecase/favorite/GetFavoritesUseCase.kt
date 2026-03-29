package com.subenoeva.velvet.core.domain.usecase.favorite

import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.domain.repository.MovieRepository
import com.subenoeva.velvet.core.domain.usecase.FlowUseCase
import com.subenoeva.velvet.core.domain.usecase.NoParams
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val repository: MovieRepository
) : FlowUseCase<NoParams, List<Movie>>() {
    override fun execute(params: NoParams) = repository.getFavorites()
}
