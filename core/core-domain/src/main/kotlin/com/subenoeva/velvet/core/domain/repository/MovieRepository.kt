package com.subenoeva.velvet.core.domain.repository

import androidx.paging.PagingData
import com.subenoeva.velvet.core.common.result.Result
import com.subenoeva.velvet.core.domain.model.Cast
import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.domain.model.MovieDetail
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getTrending(): Flow<Result<List<Movie>>>
    fun getPopular(): Flow<PagingData<Movie>>
    fun getTopRated(): Flow<PagingData<Movie>>
    fun getUpcoming(): Flow<PagingData<Movie>>
    fun getCategoryPreview(category: String, limit: Int = 10): Flow<Result<List<Movie>>>
    fun search(query: String): Flow<PagingData<Movie>>
    fun getMovieDetail(movieId: Int): Flow<Result<MovieDetail>>
    fun getCast(movieId: Int): Flow<Result<List<Cast>>>
    fun getSimilarMovies(movieId: Int): Flow<Result<List<Movie>>>
    fun getFavorites(): Flow<List<Movie>>
    suspend fun isFavorite(movieId: Int): Boolean
    suspend fun toggleFavorite(movie: Movie)
}
