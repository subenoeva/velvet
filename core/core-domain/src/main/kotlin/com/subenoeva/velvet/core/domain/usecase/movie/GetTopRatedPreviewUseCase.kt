package com.subenoeva.velvet.core.domain.usecase.movie

import com.subenoeva.velvet.core.common.result.Result
import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.domain.repository.MovieRepository
import com.subenoeva.velvet.core.domain.usecase.FlowUseCase
import com.subenoeva.velvet.core.domain.usecase.NoParams
import javax.inject.Inject

class GetTopRatedPreviewUseCase @Inject constructor(
    private val repository: MovieRepository
) : FlowUseCase<NoParams, Result<List<Movie>>>() {
    override fun execute(params: NoParams) = repository.getCategoryPreview("top_rated")
}
