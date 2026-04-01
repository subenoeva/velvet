package com.subenoeva.velvet.core.network.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.subenoeva.velvet.core.common.dispatcher.DispatcherProvider
import com.subenoeva.velvet.core.common.result.Result
import com.subenoeva.velvet.core.domain.model.Cast
import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.domain.model.MovieDetail
import com.subenoeva.velvet.core.domain.repository.MovieRepository
import com.subenoeva.velvet.core.network.api.TmdbApiService
import com.subenoeva.velvet.core.network.mapper.toDomain
import com.subenoeva.velvet.core.network.paging.PopularMoviesPagingSource
import com.subenoeva.velvet.core.network.paging.SearchMoviesPagingSource
import com.subenoeva.velvet.core.network.paging.TopRatedMoviesPagingSource
import com.subenoeva.velvet.core.network.paging.UpcomingMoviesPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

private const val PAGE_SIZE = 20

class MovieRepositoryImpl @Inject constructor(
    private val apiService: TmdbApiService,
    private val dispatchers: DispatcherProvider
) : MovieRepository {

    override fun getTrending(): Flow<Result<List<Movie>>> = flow {
        emit(Result.Loading)
        try {
            val movies = apiService.getTrending().results.map { it.toDomain() }
            emit(Result.Success(movies))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(dispatchers.io)

    override fun getPopular(): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = { PopularMoviesPagingSource(apiService) }
    ).flow

    override fun getTopRated(): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = { TopRatedMoviesPagingSource(apiService) }
    ).flow

    override fun getUpcoming(): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = { UpcomingMoviesPagingSource(apiService) }
    ).flow

    override fun search(query: String): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = { SearchMoviesPagingSource(apiService, query) }
    ).flow

    override fun getMovieDetail(movieId: Int): Flow<Result<MovieDetail>> = flow {
        emit(Result.Loading)
        try {
            val detail = apiService.getMovieDetail(movieId).toDomain()
            emit(Result.Success(detail))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(dispatchers.io)

    override fun getCast(movieId: Int): Flow<Result<List<Cast>>> = flow {
        emit(Result.Loading)
        try {
            val cast = apiService.getCredits(movieId).cast.map { it.toDomain() }
            emit(Result.Success(cast))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(dispatchers.io)

    override fun getSimilarMovies(movieId: Int): Flow<Result<List<Movie>>> = flow {
        emit(Result.Loading)
        try {
            val movies = apiService.getSimilarMovies(movieId).results.map { it.toDomain() }
            emit(Result.Success(movies))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(dispatchers.io)

    // Implementados en Fase 5 (core-database)
    override fun getFavorites(): Flow<List<Movie>> =
        throw NotImplementedError("getFavorites requires core-database (Phase 5)")

    override suspend fun isFavorite(movieId: Int): Boolean =
        throw NotImplementedError("isFavorite requires core-database (Phase 5)")

    override suspend fun toggleFavorite(movie: Movie) =
        throw NotImplementedError("toggleFavorite requires core-database (Phase 5)")
}
