package com.subenoeva.velvet.core.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.network.api.TmdbApiService
import com.subenoeva.velvet.core.network.mapper.toDomain

class UpcomingMoviesPagingSource(
    private val apiService: TmdbApiService
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        return try {
            val response = apiService.getUpcoming(page)
            val movies = response.results.map { it.toDomain() }
            LoadResult.Page(
                data = movies,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= response.totalPages) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
}
