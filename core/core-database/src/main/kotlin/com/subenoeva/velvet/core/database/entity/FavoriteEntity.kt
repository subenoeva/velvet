package com.subenoeva.velvet.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val movieId: Int,
    val title: String,
    val posterPath: String?,
    val voteAverage: Double,
    val releaseDate: String
)
