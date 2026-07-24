package com.app.tastybuds.di

import android.util.Log
import com.app.tastybuds.common.TastyBudsApiService
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

            // Local Spring Boot backend needs no Supabase apikey/Authorization/Prefer headers.
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
        // Local Spring Boot backend. 10.0.2.2 is the Android emulator's alias for the host
        // machine's localhost. For a physical device, replace with your machine's LAN IP
        // (e.g. http://192.168.1.20:8080/api/v1/) and ensure both are on the same network.
        val baseUrl = "http://10.0.2.2:8080/api/v1/"
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

