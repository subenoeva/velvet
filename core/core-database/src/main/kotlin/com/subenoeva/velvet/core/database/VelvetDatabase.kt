package com.subenoeva.velvet.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.subenoeva.velvet.core.database.dao.FavoriteDao
import com.subenoeva.velvet.core.database.dao.MovieDao
import com.subenoeva.velvet.core.database.dao.RemoteKeysDao
import com.subenoeva.velvet.core.database.entity.CastEntity
import com.subenoeva.velvet.core.database.entity.FavoriteEntity
import com.subenoeva.velvet.core.database.entity.MovieCategoryEntity
import com.subenoeva.velvet.core.database.entity.MovieDetailEntity
import com.subenoeva.velvet.core.database.entity.MovieEntity
import com.subenoeva.velvet.core.database.entity.RemoteKeysEntity

@Database(
    entities = [
        MovieEntity::class,
        MovieDetailEntity::class,
        CastEntity::class,
        MovieCategoryEntity::class,
        RemoteKeysEntity::class,
        FavoriteEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VelvetDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}
