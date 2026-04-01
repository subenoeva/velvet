package com.subenoeva.velvet.core.network.mapper

import com.subenoeva.velvet.core.database.entity.CastEntity
import com.subenoeva.velvet.core.database.entity.MovieDetailEntity
import com.subenoeva.velvet.core.database.entity.MovieEntity
import com.subenoeva.velvet.core.network.dto.CastDto
import com.subenoeva.velvet.core.network.dto.MovieDetailDto
import com.subenoeva.velvet.core.network.dto.MovieDto

fun MovieDto.toEntity(): MovieEntity = MovieEntity(
    id = id,
    title = title,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    voteAverage = voteAverage,
    releaseDate = releaseDate,
    genreIds = genreIds.joinToString(",")
)

fun MovieDetailDto.toEntity(): MovieDetailEntity = MovieDetailEntity(
    id = id,
    title = title,
    tagline = tagline,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    voteAverage = voteAverage,
    releaseDate = releaseDate,
    runtime = runtime,
    genreIds = genres.joinToString(",") { it.id.toString() },
    genreNames = genres.joinToString(",") { it.name },
    trailerKey = videos?.results?.firstOrNull { it.site == "YouTube" && it.type == "Trailer" }?.key
)

fun CastDto.toEntity(movieId: Int): CastEntity = CastEntity(
    id = id,
    movieId = movieId,
    name = name,
    character = character,
    profilePath = profilePath
)
