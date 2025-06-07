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

    @GET("sale_items")
    suspend fun getDeals(
        @Query("select") select: String = "*"
    ): List<DealResponse>

    @GET("restaurants")
    suspend fun searchRestaurants(
        @Query("name") nameQuery: String,
        @Query("select") select: String = "*"
    ): List<RestaurantResponse>

    @GET("restaurants")
    suspend fun getRestaurantsByCategory(
        @Query("category_ids") categoryFilter: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "rating.desc"
    ): List<RestaurantResponse>

    @GET("restaurants")
    suspend fun getRecommendedRestaurants(
        @Query("select") select: String = "*",
        @Query("is_open") isOpen: String = "eq.true",
        @Query("order") order: String = "rating.desc",
        @Query("limit") limit: String = "20"
    ): List<RestaurantResponse>

    @GET("restaurants")
    suspend fun getTopRestaurantsByCategory(
        @Query("category_ids") categoryIds: String,
        @Query("order") order: String = "rating.desc,review_count.desc",
        @Query("limit") limit: Int = 6,
        @Query("select") select: String = "*"
    ): List<CategoryRestaurantResponse>

    @GET("menu_items")
    suspend fun getMenuItemsByCategory(
        @Query("category_id") categoryId: String,
        @Query("is_popular") isPopular: String = "eq.true",
        @Query("order") order: String = "rating.desc,review_count.desc",
        @Query("limit") limit: Int = 6,
        @Query("select") select: String = "*,restaurants(name,delivery_time,id)"
    ): List<CategoryMenuItemResponse>

    @GET("restaurants")
    suspend fun getRecommendedRestaurantsByCategory(
        @Query("category_ids") categoryIds: String,
        @Query("rating") minRating: String = "gte.4.0",
        @Query("order") order: String = "review_count.desc",
        @Query("limit") limit: Int = 5,
        @Query("offset") offset: Int = 6,
        @Query("select") select: String = "*"
    ): List<CategoryRestaurantResponse>

    @GET("restaurants")
    suspend fun getFilteredRestaurants(
        @Query("category_ids") categoryIds: String,
        @Query("is_freeship") isFreeship: String? = null,
        @Query("order") order: String = "rating.desc",
        @Query("limit") limit: Int = 10,
        @Query("select") select: String = "*"
    ): List<CategoryRestaurantResponse>
}