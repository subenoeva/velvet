package com.subenoeva.velvet.core.domain.usecase.movie

import com.subenoeva.velvet.core.common.result.Result
import com.subenoeva.velvet.core.domain.model.Cast
import com.subenoeva.velvet.core.domain.repository.MovieRepository
import com.subenoeva.velvet.core.domain.usecase.FlowUseCase
import javax.inject.Inject

class GetMovieCastUseCase @Inject constructor(
    private val repository: MovieRepository
) : FlowUseCase<Int, Result<List<Cast>>>() {
    override fun execute(params: Int) = repository.getCast(params)
}
