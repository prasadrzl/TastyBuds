package com.app.tastybuds.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Generic key→JSON cache row. Used to serve last-known data when the network is unavailable. */
@Entity(tableName = "cached_response")
data class CachedResponseEntity(
    @PrimaryKey val key: String,
    val json: String,
    val updatedAt: Long
)
