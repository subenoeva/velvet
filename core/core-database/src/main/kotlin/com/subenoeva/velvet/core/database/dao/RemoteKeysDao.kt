package com.subenoeva.velvet.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.subenoeva.velvet.core.database.entity.RemoteKeysEntity

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKeys: RemoteKeysEntity)

    @Query("SELECT * FROM remote_keys WHERE category = :category")
    suspend fun getRemoteKeys(category: String): RemoteKeysEntity?

    @Query("DELETE FROM remote_keys WHERE category = :category")
    suspend fun clearRemoteKeys(category: String)
}
