package com.app.tastybuds.di

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.repo.UserRepository
import com.app.tastybuds.data.repo.UserRepositoryImpl
import com.app.tastybuds.domain.UserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {

    @Singleton
    @Provides
    fun provideUserRepository(
        apiService: TastyBudsApiService
    ): UserRepository {
        return UserRepositoryImpl(apiService)
    }

    @Provides
    fun provideUserUseCase(userRepository: UserRepository
    ): UserUseCase {
        return UserUseCase(userRepository)
    }
}