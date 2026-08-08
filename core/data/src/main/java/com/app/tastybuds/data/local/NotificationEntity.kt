package com.app.tastybuds.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val type: String,
    val timestamp: Long,
    val read: Boolean,
    val deepLink: String?
)
