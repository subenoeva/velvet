package com.subenoeva.velvet.core.domain.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val tagline: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val releaseDate: String,
    val runtime: Int,
    val genres: List<Genre>,
    val videos: List<Video>
)
