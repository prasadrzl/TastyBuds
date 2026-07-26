package com.app.tastybuds.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.repo.UserLocalDataSource
import com.app.tastybuds.data.repo.UserRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tastybuds_preferences")

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideLocalDataSource(
        context: Context,
        dataStore: DataStore<Preferences>
    ): UserLocalDataSource = UserLocalDataSource(context, dataStore)

    @Provides
    @Singleton
    fun provideRemoteDataSource(
        apiService: TastyBudsApiService
    ): UserRemoteDataSource = UserRemoteDataSource(apiService)
}