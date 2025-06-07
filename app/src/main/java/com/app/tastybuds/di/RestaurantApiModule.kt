package com.app.tastybuds.di

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.repo.RestaurantRepository
import com.app.tastybuds.data.repo.RestaurantRepositoryImpl
import com.app.tastybuds.domain.RestaurantUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RestaurantModule {

    @Singleton
    @Provides
    fun provideRestaurantRepository(
        apiService: TastyBudsApiService
    ): RestaurantRepository {
        return RestaurantRepositoryImpl(apiService)
    }

    @Provides
    fun provideRestaurantUseCase(
        restaurantRepository: RestaurantRepository
    ): RestaurantUseCase {
        return RestaurantUseCase(restaurantRepository)
    }
}