package com.app.tastybuds.di

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.repo.VouchersRepository
import com.app.tastybuds.data.repo.VouchersRepositoryImpl
import com.app.tastybuds.domain.VouchersUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VouchersModule {

    @Singleton
    @Provides
    fun provideVouchersRepository(
        apiService: TastyBudsApiService
    ): VouchersRepository {
        return VouchersRepositoryImpl(apiService)
    }

    @Provides
    fun provideVouchersUseCase(
        vouchersRepository: VouchersRepository
    ): VouchersUseCase {
        return VouchersUseCase(vouchersRepository)
    }
}