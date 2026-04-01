package com.subenoeva.velvet.core.database.entity

import androidx.room.Entity

@Entity(tableName = "cast_members", primaryKeys = ["id", "movieId"])
data class CastEntity(
    val id: Int,
    val movieId: Int,
    val name: String,
    val character: String,
    val profilePath: String?,
    val lastUpdated: Long = System.currentTimeMillis()
)
