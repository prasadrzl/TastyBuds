package com.app.tastybuds.di

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.SearchResultsMapper
import com.app.tastybuds.data.repo.FoodDetailsRepository
import com.app.tastybuds.data.repo.FoodDetailsRepositoryImpl
import com.app.tastybuds.data.repo.RestaurantDetailsRepository
import com.app.tastybuds.data.repo.RestaurantDetailsRepositoryImpl
import com.app.tastybuds.data.repo.RestaurantRepository
import com.app.tastybuds.data.repo.RestaurantRepositoryImpl
import com.app.tastybuds.data.repo.SearchRepository
import com.app.tastybuds.data.repo.SearchRepositoryImpl
import com.app.tastybuds.domain.FoodDetailsUseCase
import com.app.tastybuds.domain.RestaurantDetailsUseCase
import com.app.tastybuds.domain.RestaurantUseCase
import com.app.tastybuds.domain.SearchResultsUseCase
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

    @Provides
    fun provideSearchResultsRepository(
        apiService: TastyBudsApiService
    ): SearchRepository {
        return SearchRepositoryImpl(apiService)
    }

    @Provides
    fun provideSearchUseCase(
        searchRepository: SearchRepository,
        searchResultsMapper: SearchResultsMapper
    ): SearchResultsUseCase {
        return SearchResultsUseCase(searchRepository, searchResultsMapper)
    }

    @Singleton
    @Provides
    fun provideRestaurantDetailsRepository(
        apiService: TastyBudsApiService
    ): RestaurantDetailsRepository {
        return RestaurantDetailsRepositoryImpl(apiService)
    }

    @Provides
    fun provideRestaurantDetailsUseCase(
        restaurantDetailsRepository: RestaurantDetailsRepository
    ): RestaurantDetailsUseCase {
        return RestaurantDetailsUseCase(restaurantDetailsRepository)
    }

    @Singleton
    @Provides
    fun provideFoodDetailsRepository(
        apiService: TastyBudsApiService
    ): FoodDetailsRepository {
        return FoodDetailsRepositoryImpl(apiService)
    }

    @Provides
    fun provideFoodDetailsUseCase(
        repository: FoodDetailsRepository
    ): FoodDetailsUseCase {
        return FoodDetailsUseCase(repository)
    }
}