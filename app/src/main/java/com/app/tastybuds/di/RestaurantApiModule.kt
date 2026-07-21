package com.app.tastybuds.di

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.mapper.SearchResultsMapper
import com.app.tastybuds.domain.repository.FoodDetailsRepository
import com.app.tastybuds.data.repo.FoodDetailsRepositoryImpl
import com.app.tastybuds.domain.repository.RestaurantDetailsRepository
import com.app.tastybuds.data.repo.RestaurantDetailsRepositoryImpl
import com.app.tastybuds.domain.repository.RestaurantRepository
import com.app.tastybuds.data.repo.RestaurantRepositoryImpl
import com.app.tastybuds.domain.repository.SearchRepository
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
        apiService: TastyBudsApiService,
        searchResultsMapper: SearchResultsMapper
    ): SearchRepository {
        return SearchRepositoryImpl(apiService, searchResultsMapper)
    }

    @Provides
    fun provideSearchUseCase(
        searchRepository: SearchRepository
    ): SearchResultsUseCase {
        return SearchResultsUseCase(searchRepository)
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