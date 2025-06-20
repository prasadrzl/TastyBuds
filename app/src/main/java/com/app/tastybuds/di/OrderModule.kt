package com.app.tastybuds.di

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.repo.OrderRepository
import com.app.tastybuds.data.repo.OrderRepositoryImpl
import com.app.tastybuds.domain.OrderUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OrderModule {

    @Provides
    @Singleton
    fun provideOrderRepository(
        orderApiService: TastyBudsApiService
    ): OrderRepository {
        return OrderRepositoryImpl(orderApiService)
    }

    @Provides
    @Singleton
    fun provideOrderUseCase(
        orderRepository: OrderRepository
    ): OrderUseCase {
        return OrderUseCase(orderRepository)
    }
}