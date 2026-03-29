package com.subenoeva.velvet.core.domain.usecase.movie

import com.subenoeva.velvet.core.common.result.Result
import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.domain.repository.MovieRepository
import com.subenoeva.velvet.core.domain.usecase.FlowUseCase
import javax.inject.Inject

class GetSimilarMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) : FlowUseCase<Int, Result<List<Movie>>>() {
    override fun execute(params: Int) = repository.getSimilarMovies(params)
}
