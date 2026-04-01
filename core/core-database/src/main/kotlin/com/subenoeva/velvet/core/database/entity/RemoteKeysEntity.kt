package com.subenoeva.velvet.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeysEntity(
    @PrimaryKey val category: String,
    val nextPage: Int?
)
