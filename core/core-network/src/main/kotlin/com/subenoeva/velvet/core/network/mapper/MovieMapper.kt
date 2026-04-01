package com.subenoeva.velvet.core.network.mapper

import com.subenoeva.velvet.core.domain.model.Cast
import com.subenoeva.velvet.core.domain.model.Genre
import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.domain.model.MovieDetail
import com.subenoeva.velvet.core.domain.model.Video
import com.subenoeva.velvet.core.network.dto.CastDto
import com.subenoeva.velvet.core.network.dto.MovieDetailDto
import com.subenoeva.velvet.core.network.dto.MovieDto

fun MovieDto.toDomain(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    voteAverage = voteAverage,
    releaseDate = releaseDate,
    genreIds = genreIds
)

fun MovieDetailDto.toDomain(): MovieDetail = MovieDetail(
    id = id,
    title = title,
    tagline = tagline,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    voteAverage = voteAverage,
    releaseDate = releaseDate,
    runtime = runtime,
    genres = genres.map { Genre(it.id, it.name) },
    videos = videos?.results?.map { Video(it.id, it.key, it.name, it.site, it.type) } ?: emptyList()
)

fun CastDto.toDomain(): Cast = Cast(
    id = id,
    name = name,
    character = character,
    profilePath = profilePath
)
