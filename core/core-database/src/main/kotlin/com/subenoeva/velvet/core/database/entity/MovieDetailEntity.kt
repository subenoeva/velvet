package com.subenoeva.velvet.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_details")
data class MovieDetailEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val tagline: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val releaseDate: String,
    val runtime: Int,
    val genreIds: String = "",
    val genreNames: String = "",
    val trailerKey: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
