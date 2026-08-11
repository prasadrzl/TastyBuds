package com.app.tastybuds.di

import com.app.tastybuds.data.repo.AuthTokenStore
import com.app.tastybuds.data.repo.ChatRepositoryImpl
import com.app.tastybuds.data.repo.ChatWebSocketDataSource
import com.app.tastybuds.domain.ChatUseCase
import com.app.tastybuds.domain.repository.ChatRepository
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Singleton
    @Provides
    fun provideChatRepository(
        dataSource: ChatWebSocketDataSource,
        tokenStore: AuthTokenStore,
        moshi: Moshi
    ): ChatRepository = ChatRepositoryImpl(dataSource, tokenStore, moshi)

    @Provides
    fun provideChatUseCase(chatRepository: ChatRepository): ChatUseCase =
        ChatUseCase(chatRepository)
}
