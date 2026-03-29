package com.subenoeva.velvet.core.domain.usecase.favorite

import com.subenoeva.velvet.core.domain.repository.MovieRepository
import com.subenoeva.velvet.core.domain.usecase.UseCase
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val repository: MovieRepository
) : UseCase<Int, Boolean>() {
    override suspend fun execute(params: Int) = repository.isFavorite(params)
}
