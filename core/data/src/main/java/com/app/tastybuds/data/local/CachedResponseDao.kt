package com.app.tastybuds.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedResponseDao {

    @Query("SELECT * FROM cached_response WHERE `key` = :key")
    suspend fun get(key: String): CachedResponseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entity: CachedResponseEntity)
}
