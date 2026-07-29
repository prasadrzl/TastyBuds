package com.app.tastybuds.di

import android.util.Log
import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.core.data.BuildConfig
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
                .addHeader("Content-Type", "application/json")
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

        val timeoutSeconds = BuildConfig.NETWORK_TIMEOUT_SECONDS
        val client = OkHttpClient.Builder()
            .connectionPool(connectionPool)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(customInterceptor)
            .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
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
        // Base URL comes from config.properties via BuildConfig (see :core:data build).
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
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

