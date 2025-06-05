package com.app.tastybuds.common

import com.app.tastybuds.data.model.*
import retrofit2.http.GET
import retrofit2.http.Query

interface TastyBudsApiService {

    @GET("banners")
    suspend fun getBanners(
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc"
    ): List<BannerResponse>

    @GET("categories")
    suspend fun getCategories(
        @Query("select") select: String = "*",
        @Query("order") order: String = "name.asc"
    ): List<CategoryResponse>

    @GET("vouchers")
    suspend fun getVoucherCount(
        @Query("user_id") userId: String = "",
        @Query("select") select: String = "*"
    ): List<VoucherCountResponse>

    @GET("collections")
    suspend fun getCollections(
        @Query("select") select: String = "*",
        @Query("order") order: String = "title.asc"
    ): List<CollectionResponse>

    @GET("restaurants")
    suspend fun getRecommendedRestaurants(
        @Query("select") select: String = "*",
        @Query("is_open") isOpen: String = "eq.true",
        @Query("order") order: String = "rating.desc",
        @Query("limit") limit: String = "20"
    ): List<RestaurantResponse>

    // Keep this as Response<> only if you're unsure the table exists
    @GET("sale_items")
    suspend fun getDeals(
        @Query("select") select: String = "*"
    ): List<DealResponse>  // Changed back to consistent approach

    @GET("restaurants")
    suspend fun searchRestaurants(
        @Query("name") nameQuery: String,
        @Query("select") select: String = "*"
    ): List<RestaurantResponse>
}