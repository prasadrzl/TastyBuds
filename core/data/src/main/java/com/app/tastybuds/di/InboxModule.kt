package com.app.tastybuds.di

import android.content.Context
import androidx.room.Room
import com.app.tastybuds.common.TastyBudsApiService
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

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): TastyBudsDatabase =
        Room.databaseBuilder(context, TastyBudsDatabase::class.java, "tastybuds.db").build()

    @Provides
    fun provideNotificationDao(db: TastyBudsDatabase): NotificationDao = db.notificationDao()

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
