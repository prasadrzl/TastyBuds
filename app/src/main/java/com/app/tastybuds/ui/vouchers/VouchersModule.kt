package com.app.tastybuds.ui.vouchers

import com.app.tastybuds.common.TastyBudsApiService
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