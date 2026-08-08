package com.app.tastybuds.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NotificationEntity::class], version = 1, exportSchema = false)
abstract class TastyBudsDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
}
