package com.subenoeva.velvet.core.network.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.subenoeva.velvet.core.common.dispatcher.DispatcherProvider
import com.subenoeva.velvet.core.common.result.Result
import com.subenoeva.velvet.core.database.VelvetDatabase
import com.subenoeva.velvet.core.database.entity.MovieCategoryEntity
import com.subenoeva.velvet.core.database.mapper.toDomain
import com.subenoeva.velvet.core.database.mapper.toFavoriteEntity
import com.subenoeva.velvet.core.database.mapper.toMovie
import com.subenoeva.velvet.core.domain.model.Cast
import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.domain.model.MovieDetail
import com.subenoeva.velvet.core.domain.repository.MovieRepository
import com.subenoeva.velvet.core.network.api.TmdbApiService
import com.subenoeva.velvet.core.network.mapper.toEntity
import com.subenoeva.velvet.core.network.mediator.PopularMoviesRemoteMediator
import com.subenoeva.velvet.core.network.mediator.SearchMoviesRemoteMediator
import com.subenoeva.velvet.core.network.mediator.TopRatedMoviesRemoteMediator
import com.subenoeva.velvet.core.network.mediator.UpcomingMoviesRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val PAGE_SIZE = 20
private const val TRENDING_TTL_MS = 30 * 60 * 1000L

@OptIn(ExperimentalPagingApi::class)
class MovieRepositoryImpl @Inject constructor(
    private val apiService: TmdbApiService,
    private val database: VelvetDatabase,
    private val dispatchers: DispatcherProvider
) : MovieRepository {

    private val movieDao = database.movieDao()
    private val favoriteDao = database.favoriteDao()

    override fun getTrending(): Flow<Result<List<Movie>>> = flow {
        emit(Result.Loading)
        try {
            val lastUpdated = movieDao.getLastUpdatedForCategory("trending")
            val isStale =
                lastUpdated == null || System.currentTimeMillis() - lastUpdated > TRENDING_TTL_MS
            if (isStale) {
                val dtos = apiService.getTrending().results
                val entities = dtos.map { it.toEntity() }
                movieDao.upsertMovies(entities)
                movieDao.clearCategoryMovies("trending")
                movieDao.insertCategoryMovies(
                    entities.mapIndexed { index, entity ->
                        MovieCategoryEntity(entity.id, "trending", 1, index)
                    }
                )
            }
            val cached = movieDao.getMoviesByCategoryList("trending")
            if (cached.isNotEmpty()) {
                emit(Result.Success(cached.map { it.toDomain() }))
            } else {
                emit(Result.Error(Exception("No trending movies available")))
            }
        } catch (e: Exception) {
            val cached = movieDao.getMoviesByCategoryList("trending")
            if (cached.isNotEmpty()) {
                emit(Result.Success(cached.map { it.toDomain() }))
            } else {
                emit(Result.Error(e))
            }
        }
    }.flowOn(dispatchers.io)

    override fun getPopular(): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        remoteMediator = PopularMoviesRemoteMediator(apiService, database),
        pagingSourceFactory = { movieDao.getMoviesByCategory("popular") }
    ).flow.map { pagingData -> pagingData.map { it.toDomain() } }

    override fun getTopRated(): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        remoteMediator = TopRatedMoviesRemoteMediator(apiService, database),
        pagingSourceFactory = { movieDao.getMoviesByCategory("top_rated") }
    ).flow.map { pagingData -> pagingData.map { it.toDomain() } }

    override fun getUpcoming(): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        remoteMediator = UpcomingMoviesRemoteMediator(apiService, database),
        pagingSourceFactory = { movieDao.getMoviesByCategory("upcoming") }
    ).flow.map { pagingData -> pagingData.map { it.toDomain() } }

    override fun getCategoryPreview(category: String, limit: Int): Flow<Result<List<Movie>>> = flow {
        emit(Result.Loading)
        try {
            val lastUpdated = movieDao.getLastUpdatedForCategory(category)
            val isStale = lastUpdated == null || System.currentTimeMillis() - lastUpdated > TRENDING_TTL_MS
            if (isStale) {
                val response = when (category) {
                    "popular"   -> apiService.getPopular(1)
                    "top_rated" -> apiService.getTopRated(1)
                    "upcoming"  -> apiService.getUpcoming(1)
                    else        -> throw IllegalArgumentException("Unknown category: $category")
                }
                val entities = response.results.map { it.toEntity() }
                movieDao.upsertMovies(entities)
                movieDao.clearCategoryMovies(category)
                movieDao.insertCategoryMovies(
                    entities.mapIndexed { index, entity ->
                        MovieCategoryEntity(entity.id, category, 1, index)
                    }
                )
            }
        } catch (networkError: Exception) {
            // stored to propagate if cache is empty
            val fetchError: Exception = networkError
            movieDao.getMoviesByCategoryPreview(category, limit)
                .collect { entities ->
                    if (entities.isNotEmpty())
                        emit(Result.Success(entities.map { it.toDomain() }))
                    else
                        emit(Result.Error(fetchError))
                }
            return@flow
        }
        movieDao.getMoviesByCategoryPreview(category, limit)
            .collect { entities ->
                if (entities.isNotEmpty())
                    emit(Result.Success(entities.map { it.toDomain() }))
                else
                    emit(Result.Error(Exception("No $category movies available")))
            }
    }.flowOn(dispatchers.io)

    override fun search(query: String): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        remoteMediator = SearchMoviesRemoteMediator(query, apiService, database),
        pagingSourceFactory = { movieDao.getMoviesByCategory("search:$query") }
    ).flow.map { pagingData -> pagingData.map { it.toDomain() } }

    override fun getMovieDetail(movieId: Int): Flow<Result<MovieDetail>> = flow {
        emit(Result.Loading)
        try {
            val cached = movieDao.getMovieDetail(movieId)
            if (cached != null) emit(Result.Success(cached.toDomain()))
            val detail = apiService.getMovieDetail(movieId).toEntity()
            movieDao.upsertMovieDetail(detail)
            emit(Result.Success(movieDao.getMovieDetail(movieId)!!.toDomain()))
        } catch (e: Exception) {
            val cached = movieDao.getMovieDetail(movieId)
            if (cached != null)
                emit(Result.Success(cached.toDomain()))
            else
                emit(Result.Error(e))
        }
    }.flowOn(dispatchers.io)

    override fun getCast(movieId: Int): Flow<Result<List<Cast>>> = flow {
        emit(Result.Loading)
        try {
            val cached = movieDao.getCast(movieId)
            if (cached.isNotEmpty()) emit(Result.Success(cached.map { it.toDomain() }))
            val cast = apiService.getCredits(movieId).cast.map { it.toEntity(movieId) }
            movieDao.upsertCast(cast)
            emit(Result.Success(movieDao.getCast(movieId).map { it.toDomain() }))
        } catch (e: Exception) {
            val cached = movieDao.getCast(movieId)
            if (cached.isNotEmpty())
                emit(Result.Success(cached.map { it.toDomain() }))
            else
                emit(Result.Error(e))
        }
    }.flowOn(dispatchers.io)

    override fun getSimilarMovies(movieId: Int): Flow<Result<List<Movie>>> = flow {
        emit(Result.Loading)
        try {
            val movies = apiService.getSimilarMovies(movieId).results.map { it.toEntity() }
            movieDao.upsertMovies(movies)
            emit(Result.Success(movies.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(dispatchers.io)

    override fun getFavorites(): Flow<List<Movie>> =
        favoriteDao.getAllFavorites().map { list -> list.map { it.toMovie() } }

    override suspend fun isFavorite(movieId: Int): Boolean =
        favoriteDao.isFavorite(movieId)

    override suspend fun toggleFavorite(movie: Movie) {
        if (favoriteDao.isFavorite(movie.id)) {
            favoriteDao.removeFavorite(movie.toFavoriteEntity())
        } else {
            favoriteDao.insertFavorite(movie.toFavoriteEntity())
        }
    }
}
