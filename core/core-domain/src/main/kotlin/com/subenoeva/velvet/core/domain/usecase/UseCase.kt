package com.subenoeva.velvet.core.domain.usecase

import kotlinx.coroutines.flow.Flow

abstract class UseCase<in P, out R> {
    suspend operator fun invoke(params: P): R = execute(params)
    protected abstract suspend fun execute(params: P): R
}

abstract class FlowUseCase<in P, out R> {
    operator fun invoke(params: P): Flow<R> = execute(params)
    protected abstract fun execute(params: P): Flow<R>
}

object NoParams
