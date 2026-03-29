package com.subenoeva.velvet.core.domain.usecase.movie

import com.subenoeva.velvet.core.common.result.Result
import com.subenoeva.velvet.core.domain.model.MovieDetail
import com.subenoeva.velvet.core.domain.repository.MovieRepository
import com.subenoeva.velvet.core.domain.usecase.FlowUseCase
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val repository: MovieRepository
) : FlowUseCase<Int, Result<MovieDetail>>() {
    override fun execute(params: Int) = repository.getMovieDetail(params)
}
