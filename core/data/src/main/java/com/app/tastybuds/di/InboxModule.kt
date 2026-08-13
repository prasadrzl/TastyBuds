package com.app.tastybuds.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.local.CachedResponseDao
import com.app.tastybuds.data.local.NotificationDao
import com.app.tastybuds.data.local.TastyBudsDatabase
import com.app.tastybuds.data.repo.DeviceRepositoryImpl
import com.app.tastybuds.data.repo.InboxRepositoryImpl
import com.app.tastybuds.domain.InboxUseCase
import com.app.tastybuds.domain.repository.DeviceRepository
import com.app.tastybuds.domain.repository.InboxRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InboxModule {

    // v1 -> v2 adds the generic offline cache table; keeps the notifications inbox intact.
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS cached_response " +
                    "(`key` TEXT NOT NULL PRIMARY KEY, `json` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL)"
            )
        }
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): TastyBudsDatabase =
        Room.databaseBuilder(context, TastyBudsDatabase::class.java, "tastybuds.db")
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    fun provideNotificationDao(db: TastyBudsDatabase): NotificationDao = db.notificationDao()

    @Provides
    fun provideCachedResponseDao(db: TastyBudsDatabase): CachedResponseDao = db.cachedResponseDao()

    @Singleton
    @Provides
    fun provideInboxRepository(dao: NotificationDao): InboxRepository = InboxRepositoryImpl(dao)

    @Provides
    fun provideInboxUseCase(repository: InboxRepository): InboxUseCase = InboxUseCase(repository)

    @Singleton
    @Provides
    fun provideDeviceRepository(apiService: TastyBudsApiService): DeviceRepository =
        DeviceRepositoryImpl(apiService)
}
