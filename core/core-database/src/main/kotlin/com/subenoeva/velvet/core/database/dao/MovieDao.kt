package com.subenoeva.velvet.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.subenoeva.velvet.core.database.entity.CastEntity
import com.subenoeva.velvet.core.database.entity.MovieCategoryEntity
import com.subenoeva.velvet.core.database.entity.MovieDetailEntity
import com.subenoeva.velvet.core.database.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMovies(movies: List<MovieEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryMovies(categories: List<MovieCategoryEntity>)

    @Query("""
        SELECT movies.* FROM movies
        INNER JOIN movie_categories ON movies.id = movie_categories.movieId
        WHERE movie_categories.category = :category
        ORDER BY movie_categories.page ASC, movie_categories.`order` ASC
    """)
    fun getMoviesByCategory(category: String): PagingSource<Int, MovieEntity>

    @Query("""
        SELECT movies.* FROM movies
        INNER JOIN movie_categories ON movies.id = movie_categories.movieId
        WHERE movie_categories.category = :category
        ORDER BY movie_categories.page ASC, movie_categories.`order` ASC
    """)
    suspend fun getMoviesByCategoryList(category: String): List<MovieEntity>

    @Query("""
        SELECT movies.* FROM movies
        INNER JOIN movie_categories ON movies.id = movie_categories.movieId
        WHERE movie_categories.category = :category
        ORDER BY movie_categories.page ASC, movie_categories.`order` ASC
        LIMIT :limit
    """)
    fun getMoviesByCategoryPreview(category: String, limit: Int): Flow<List<MovieEntity>>

    @Query("DELETE FROM movie_categories WHERE category = :category")
    suspend fun clearCategoryMovies(category: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMovieDetail(detail: MovieDetailEntity)

    @Query("SELECT * FROM movie_details WHERE id = :movieId")
    suspend fun getMovieDetail(movieId: Int): MovieDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCast(cast: List<CastEntity>)

    @Query("SELECT * FROM cast_members WHERE movieId = :movieId")
    suspend fun getCast(movieId: Int): List<CastEntity>

    @Query("SELECT lastUpdated FROM movie_categories INNER JOIN movies ON movies.id = movie_categories.movieId WHERE movie_categories.category = :category LIMIT 1")
    suspend fun getLastUpdatedForCategory(category: String): Long?
}
