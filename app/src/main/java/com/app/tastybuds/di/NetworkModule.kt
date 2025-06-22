package com.app.tastybuds.di

import android.util.Log
import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.repo.HomeRepository
import com.app.tastybuds.data.repo.HomeRepositoryImpl
import com.app.tastybuds.domain.HomeUseCase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {

        val loggingInterceptor = HttpLoggingInterceptor { message ->
        }.apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val customInterceptor = { chain: okhttp3.Interceptor.Chain ->
            val originalRequest = chain.request()

            val modifiedRequest = originalRequest.newBuilder()
                .addHeader(
                    "apikey",
                    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFudXhjcG5hYWtvanRteHRya3lnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg5NDA5OTcsImV4cCI6MjA2NDUxNjk5N30.cpUGX5KxiE8K7qFMstRWQ6yZFyEli_snzcQkaZCj6sU"
                )
                .addHeader(
                    "Authorization",
                    "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFudXhjcG5hYWtvanRteHRya3lnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg5NDA5OTcsImV4cCI6MjA2NDUxNjk5N30.cpUGX5KxiE8K7qFMstRWQ6yZFyEli_snzcQkaZCj6sU"
                )
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .build()

            try {
                val response = chain.proceed(modifiedRequest)

                if (!response.isSuccessful) {
                    Log.e("NETWORK_ERROR", "HTTP Error: ${response.code} - ${response.message}")
                } else {
                    Log.d("NETWORK_DEBUG", "Response successful!")
                }
                response
            } catch (e: Exception) {
                Log.e("NETWORK_ERROR", "Exception message: ${e.message}")
                throw e
            }
        }

        val connectionPool = ConnectionPool(
            maxIdleConnections = 10,
            keepAliveDuration = 5,
            timeUnit = TimeUnit.MINUTES
        )

        val client = OkHttpClient.Builder()
            .connectionPool(connectionPool)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(customInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        return client
    }

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        val baseUrl = "https://qnuxcpnaakojtmxtrkyg.supabase.co/rest/v1/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return retrofit
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

