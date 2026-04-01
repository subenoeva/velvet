package com.subenoeva.velvet.core.database.mapper

import com.subenoeva.velvet.core.database.entity.CastEntity
import com.subenoeva.velvet.core.database.entity.FavoriteEntity
import com.subenoeva.velvet.core.database.entity.MovieDetailEntity
import com.subenoeva.velvet.core.database.entity.MovieEntity
import com.subenoeva.velvet.core.domain.model.Cast
import com.subenoeva.velvet.core.domain.model.Genre
import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.domain.model.MovieDetail
import com.subenoeva.velvet.core.domain.model.Video

fun MovieEntity.toDomain(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    voteAverage = voteAverage,
    releaseDate = releaseDate,
    genreIds = if (genreIds.isBlank()) emptyList() else genreIds.split(",").map { it.toInt() }
)

fun MovieDetailEntity.toDomain(): MovieDetail = MovieDetail(
    id = id,
    title = title,
    tagline = tagline,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    voteAverage = voteAverage,
    releaseDate = releaseDate,
    runtime = runtime,
    genres = parseGenres(genreIds, genreNames),
    videos = if (trailerKey != null) {
        listOf(Video(id = "", key = trailerKey, name = "Trailer", site = "YouTube", type = "Trailer"))
    } else emptyList()
)

fun CastEntity.toDomain(): Cast = Cast(
    id = id,
    name = name,
    character = character,
    profilePath = profilePath
)

fun FavoriteEntity.toMovie(): Movie = Movie(
    id = movieId,
    title = title,
    overview = "",
    posterPath = posterPath,
    backdropPath = null,
    voteAverage = voteAverage,
    releaseDate = releaseDate,
    genreIds = emptyList()
)

fun Movie.toFavoriteEntity(): FavoriteEntity = FavoriteEntity(
    movieId = id,
    title = title,
    posterPath = posterPath,
    voteAverage = voteAverage,
    releaseDate = releaseDate
)

private fun parseGenres(ids: String, names: String): List<Genre> {
    if (ids.isBlank() || names.isBlank()) return emptyList()
    val idList = ids.split(",")
    val nameList = names.split(",")
    return idList.zip(nameList).map { (id, name) -> Genre(id.toInt(), name) }
}
