package com.app.tastybuds.common

import com.app.tastybuds.data.model.*
import retrofit2.http.GET
import retrofit2.http.Query

interface TastyBudsApiService {
    
    @GET("banners")
    suspend fun getBanners(): List<BannerResponse>
    
    @GET("categories")
    suspend fun getCategories(): List<CategoryResponse>
    
    @GET("home_meta")
    suspend fun getVoucherCount(@Query("user_id") userId: String = "user_001"): List<VoucherCountResponse>
    
    @GET("collections")
    suspend fun getCollections(@Query("limit") limit: Int = 4): List<CollectionResponse>
    
    @GET("restaurants")
    suspend fun getRecommendedRestaurants(@Query("limit") limit: Int = 3): List<RestaurantResponse>
    
    @GET("restaurants")
    suspend fun searchRestaurants(@Query("name") name: String): List<RestaurantResponse>
    
    @GET("deals")
    suspend fun getDeals(@Query("limit") limit: Int = 3): List<DealResponse>
}