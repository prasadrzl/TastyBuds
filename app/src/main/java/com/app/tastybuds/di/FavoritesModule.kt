package com.app.tastybuds.di

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.repo.FavoritesRepository
import com.app.tastybuds.data.repo.FavoritesRepositoryImpl
import com.app.tastybuds.domain.FavoritesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FavoritesModule {

    @Provides
    @Singleton
    fun provideFavoritesRepository(
        apiService: TastyBudsApiService
    ): FavoritesRepository {
        return FavoritesRepositoryImpl(apiService)
    }

    @Provides
    fun provideFavoritesUseCase(
        favoritesRepository: FavoritesRepository
    ): FavoritesUseCase {
        return FavoritesUseCase(favoritesRepository)
    }
}