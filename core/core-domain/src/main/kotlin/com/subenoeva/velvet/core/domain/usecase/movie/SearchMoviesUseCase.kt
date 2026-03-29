package com.subenoeva.velvet.core.domain.usecase.movie

import androidx.paging.PagingData
import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.domain.repository.MovieRepository
import com.subenoeva.velvet.core.domain.usecase.FlowUseCase
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) : FlowUseCase<String, PagingData<Movie>>() {
    override fun execute(params: String) = repository.search(params)
}
