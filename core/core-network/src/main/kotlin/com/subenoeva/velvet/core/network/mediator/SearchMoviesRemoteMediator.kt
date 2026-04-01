package com.subenoeva.velvet.core.network.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.subenoeva.velvet.core.database.VelvetDatabase
import com.subenoeva.velvet.core.database.entity.MovieCategoryEntity
import com.subenoeva.velvet.core.database.entity.MovieEntity
import com.subenoeva.velvet.core.database.entity.RemoteKeysEntity
import com.subenoeva.velvet.core.network.api.TmdbApiService
import com.subenoeva.velvet.core.network.mapper.toEntity

private const val CATEGORY_PREFIX = "search"

@OptIn(ExperimentalPagingApi::class)
class SearchMoviesRemoteMediator(
    private val query: String,
    private val apiService: TmdbApiService,
    private val database: VelvetDatabase
) : RemoteMediator<Int, MovieEntity>() {

    private val category = "$CATEGORY_PREFIX:$query"
    private val movieDao = database.movieDao()
    private val remoteKeysDao = database.remoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKeys = remoteKeysDao.getRemoteKeys(category)
                    remoteKeys?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val response = apiService.searchMovies(query, page)
            val endOfPagination = page >= response.totalPages

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    movieDao.clearCategoryMovies(category)
                    remoteKeysDao.clearRemoteKeys(category)
                }
                val entities = response.results.map { it.toEntity() }
                movieDao.upsertMovies(entities)
                movieDao.insertCategoryMovies(
                    entities.mapIndexed { index, entity ->
                        MovieCategoryEntity(entity.id, category, page, index)
                    }
                )
                remoteKeysDao.insertOrReplace(
                    RemoteKeysEntity(category, if (endOfPagination) null else page + 1)
                )
            }

            MediatorResult.Success(endOfPaginationReached = endOfPagination)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
