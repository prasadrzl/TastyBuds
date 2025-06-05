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
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.ConnectionPool
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        Log.d("NETWORK_DEBUG", "Creating OkHttpClient...")

        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("HTTP_RAW", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BASIC  // Changed from BODY to BASIC to reduce logging overhead
            Log.d("NETWORK_DEBUG", "HTTP Logging interceptor created with BASIC level")
        }

        val customInterceptor = { chain: okhttp3.Interceptor.Chain ->
            Log.d("NETWORK_DEBUG", "=== INTERCEPTOR TRIGGERED ===")
            val originalRequest = chain.request()

            Log.d("NETWORK_DEBUG", "Original request URL: ${originalRequest.url}")
            Log.d("NETWORK_DEBUG", "Original request method: ${originalRequest.method}")

            // Add Supabase API key to all requests
            val modifiedRequest = originalRequest.newBuilder()
                .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFudXhjcG5hYWtvanRteHRya3lnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg5NDA5OTcsImV4cCI6MjA2NDUxNjk5N30.cpUGX5KxiE8K7qFMstRWQ6yZFyEli_snzcQkaZCj6sU")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFudXhjcG5hYWtvanRteHRya3lnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg5NDA5OTcsImV4cCI6MjA2NDUxNjk5N30.cpUGX5KxiE8K7qFMstRWQ6yZFyEli_snzcQkaZCj6sU")
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .build()

            try {
                Log.d("NETWORK_DEBUG", "About to proceed with request...")
                val startTime = System.currentTimeMillis()
                val response = chain.proceed(modifiedRequest)
                val endTime = System.currentTimeMillis()

                Log.d("NETWORK_DEBUG", "=== RESPONSE RECEIVED ===")
                Log.d("NETWORK_DEBUG", "Response received in ${endTime - startTime}ms")
                Log.d("NETWORK_DEBUG", "Response code: ${response.code}")
                Log.d("NETWORK_DEBUG", "Response message: ${response.message}")

                if (!response.isSuccessful) {
                    Log.e("NETWORK_ERROR", "HTTP Error: ${response.code} - ${response.message}")
                } else {
                    Log.d("NETWORK_DEBUG", "Response successful!")
                }

                response
            } catch (e: Exception) {
                Log.e("NETWORK_ERROR", "=== REQUEST FAILED ===")
                Log.e("NETWORK_ERROR", "Exception during request: ${e.javaClass.simpleName}")
                Log.e("NETWORK_ERROR", "Exception message: ${e.message}")
                throw e
            }
        }

        // Create a custom connection pool with more connections
        val connectionPool = ConnectionPool(
            maxIdleConnections = 10,  // Increased from default 5
            keepAliveDuration = 5,    // Keep connections alive for 5 minutes
            timeUnit = TimeUnit.MINUTES
        )

        val client = OkHttpClient.Builder()
            .connectionPool(connectionPool)  // Add custom connection pool
            .addInterceptor(loggingInterceptor)
            .addInterceptor(customInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)  // Increased timeout
            .readTimeout(30, TimeUnit.SECONDS)     // Increased timeout
            .writeTimeout(30, TimeUnit.SECONDS)    // Increased timeout
            .retryOnConnectionFailure(true)        // Enable retry
            .build()

        Log.d("NETWORK_DEBUG", "OkHttpClient created successfully with connection pool")
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
        Log.d("NETWORK_DEBUG", "Creating Retrofit with base URL: $baseUrl")

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        Log.d("NETWORK_DEBUG", "Retrofit created successfully with Kotlin Moshi support")
        return retrofit
    }

    @Singleton
    @Provides
    fun provideHomeApiService(retrofit: Retrofit): TastyBudsApiService {
        Log.d("NETWORK_DEBUG", "Creating TastyBudsApiService...")
        val apiService = retrofit.create(TastyBudsApiService::class.java)
        Log.d("NETWORK_DEBUG", "TastyBudsApiService created: $apiService")
        return apiService
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideHomeRepository(homeApiService: TastyBudsApiService): HomeRepository {
        Log.d("NETWORK_DEBUG", "Creating HomeRepository with apiService: $homeApiService")
        return HomeRepositoryImpl(homeApiService)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideHomeUseCase(homeRepository: HomeRepository): HomeUseCase {
        Log.d("NETWORK_DEBUG", "Creating HomeUseCase with repository: $homeRepository")
        return HomeUseCase(homeRepository)
    }
}