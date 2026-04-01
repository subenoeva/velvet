package com.subenoeva.velvet.core.database.entity

import androidx.room.Entity

@Entity(tableName = "movie_categories", primaryKeys = ["movieId", "category"])
data class MovieCategoryEntity(
    val movieId: Int,
    val category: String,
    val page: Int,
    val order: Int
)
