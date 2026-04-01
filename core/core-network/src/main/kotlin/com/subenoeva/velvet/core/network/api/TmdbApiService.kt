package com.subenoeva.velvet.core.network.api

import com.subenoeva.velvet.core.network.dto.CreditsResponseDto
import com.subenoeva.velvet.core.network.dto.GenreListResponseDto
import com.subenoeva.velvet.core.network.dto.MovieDetailDto
import com.subenoeva.velvet.core.network.dto.PagedResponseDto
import com.subenoeva.velvet.core.network.dto.TrendingResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

    @GET("trending/movie/week")
    suspend fun getTrending(
        @Query("language") lang: String = "es-ES"
    ): TrendingResponseDto

    @GET("movie/popular")
    suspend fun getPopular(
        @Query("page") page: Int,
        @Query("language") lang: String = "es-ES"
    ): PagedResponseDto

    @GET("movie/top_rated")
    suspend fun getTopRated(
        @Query("page") page: Int,
        @Query("language") lang: String = "es-ES"
    ): PagedResponseDto

    @GET("movie/upcoming")
    suspend fun getUpcoming(
        @Query("page") page: Int,
        @Query("language") lang: String = "es-ES"
    ): PagedResponseDto

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int
    ): PagedResponseDto

    @GET("movie/{id}")
    suspend fun getMovieDetail(
        @Path("id") id: Int,
        @Query("append_to_response") append: String = "videos"
    ): MovieDetailDto

    @GET("movie/{id}/credits")
    suspend fun getCredits(
        @Path("id") id: Int
    ): CreditsResponseDto

    @GET("movie/{id}/similar")
    suspend fun getSimilarMovies(
        @Path("id") id: Int,
        @Query("page") page: Int = 1
    ): PagedResponseDto

    @GET("genre/movie/list")
    suspend fun getGenres(): GenreListResponseDto
}
