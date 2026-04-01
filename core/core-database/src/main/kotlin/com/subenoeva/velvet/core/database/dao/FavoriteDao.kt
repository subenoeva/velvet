package com.subenoeva.velvet.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.subenoeva.velvet.core.database.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Delete
    suspend fun removeFavorite(favorite: FavoriteEntity)

    @Query("SELECT * FROM favorites ORDER BY title ASC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT COUNT(*) > 0 FROM favorites WHERE movieId = :movieId")
    suspend fun isFavorite(movieId: Int): Boolean
}
