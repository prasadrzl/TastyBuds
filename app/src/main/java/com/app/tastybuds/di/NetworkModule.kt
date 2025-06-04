package com.app.tastybuds.di

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.repo.HomeRepository
import com.app.tastybuds.data.repo.HomeRepositoryImpl
import com.app.tastybuds.domain.HomeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                // Add Supabase API key to all requests
                val request = chain.request().newBuilder()
                    .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFudXhjcG5hYWtvanRteHRya3lnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg5NDA5OTcsImV4cCI6MjA2NDUxNjk5N30.cpUGX5KxiE8K7qFMstRWQ6yZFyEli_snzcQkaZCj6sU")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://qnuxcpnaakojtmxtrkyg.supabase.co/rest/v1/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideHomeApiService(retrofit: Retrofit): TastyBudsApiService {
        return retrofit.create(TastyBudsApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideHomeRepository(homeApiService: TastyBudsApiService): HomeRepository {
        return HomeRepositoryImpl(homeApiService)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideHomeUseCase(homeRepository: HomeRepository): HomeUseCase {
        return HomeUseCase(homeRepository)
    }
}