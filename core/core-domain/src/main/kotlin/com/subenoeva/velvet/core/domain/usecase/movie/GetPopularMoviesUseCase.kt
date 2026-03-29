package com.subenoeva.velvet.core.domain.usecase.movie

import androidx.paging.PagingData
import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.domain.repository.MovieRepository
import com.subenoeva.velvet.core.domain.usecase.FlowUseCase
import com.subenoeva.velvet.core.domain.usecase.NoParams
import javax.inject.Inject

class GetPopularMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) : FlowUseCase<NoParams, PagingData<Movie>>() {
    override fun execute(params: NoParams) = repository.getPopular()
}
