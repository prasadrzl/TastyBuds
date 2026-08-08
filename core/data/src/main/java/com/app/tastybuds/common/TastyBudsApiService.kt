package com.app.tastybuds.common

import com.app.tastybuds.data.model.AddFavoriteRequest
import com.app.tastybuds.data.model.BannerResponse
import com.app.tastybuds.data.model.CategoryMenuItemResponse
import com.app.tastybuds.data.model.CategoryResponse
import com.app.tastybuds.data.model.CategoryRestaurantResponse
import com.app.tastybuds.data.model.CollectionResponse
import com.app.tastybuds.data.model.ComboResponse
import com.app.tastybuds.data.model.CreateOrderRequest
import com.app.tastybuds.data.model.DealResponse
import com.app.tastybuds.data.model.FavoriteResponse
import com.app.tastybuds.data.model.FavoriteWithMenuItemResponse
import com.app.tastybuds.data.model.FavoriteWithRestaurantResponse
import com.app.tastybuds.data.model.MenuItemResponse
import com.app.tastybuds.data.model.MenuItemWithRestaurantResponse
import com.app.tastybuds.data.model.Order
import com.app.tastybuds.data.model.RestaurantDetailsResponse
import com.app.tastybuds.data.model.RestaurantResponse
import com.app.tastybuds.data.model.RestaurantReviewResponse
import com.app.tastybuds.data.model.RestaurantVoucherResponse
import com.app.tastybuds.data.model.UpdateOrderStatusRequest
import com.app.tastybuds.data.model.UserAddress
import com.app.tastybuds.data.model.Voucher
import com.app.tastybuds.data.model.VoucherCountResponse
import com.app.tastybuds.domain.model.UpdateProfileRequest
import com.app.tastybuds.data.model.UserResponse
import com.app.tastybuds.data.model.FoodCustomizationResponse
import com.app.tastybuds.data.model.FoodDetailsResponse
import com.app.tastybuds.data.model.VoucherApiResponse
import com.app.tastybuds.data.model.VoucherUsageRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// Talks to the local Spring Boot backend (clean REST, /api/v1). The base path is set in
// NetworkModule; paths here are relative to it. Response bodies match the existing Moshi
// models 1:1 (snake_case JSON), so only paths/params changed when migrating off Supabase.
interface TastyBudsApiService {

    // ---------- Home / catalog ----------

    @GET("banners")
    suspend fun getBanners(): List<BannerResponse>

    @GET("categories")
    suspend fun getCategories(): List<CategoryResponse>

    @GET("collections")
    suspend fun getCollections(): List<CollectionResponse>

    @GET("deals")
    suspend fun getDeals(): List<DealResponse>

    @GET("vouchers/count")
    suspend fun getVoucherCount(
        @Query("userId") userId: String
    ): List<VoucherCountResponse>

    // ---------- Restaurants ----------

    @GET("restaurants/search")
    suspend fun searchRestaurants(
        @Query("name") nameQuery: String
    ): List<RestaurantResponse>

    @GET("restaurants/by-category")
    suspend fun getRestaurantsByCategory(
        @Query("categoryId") categoryId: String
    ): List<RestaurantResponse>

    @GET("restaurants/recommended")
    suspend fun getRecommendedRestaurants(
        @Query("limit") limit: Int = 20
    ): List<RestaurantResponse>

    @GET("restaurants/top-by-category")
    suspend fun getTopRestaurantsByCategory(
        @Query("categoryId") categoryId: String,
        @Query("limit") limit: Int = 6
    ): List<CategoryRestaurantResponse>

    @GET("restaurants/recommended-by-category")
    suspend fun getRecommendedRestaurantsByCategory(
        @Query("categoryId") categoryId: String,
        @Query("minRating") minRating: Double? = null,
        @Query("limit") limit: Int = 5,
        @Query("offset") offset: Int = 6
    ): List<CategoryRestaurantResponse>

    @GET("restaurants/filtered")
    suspend fun getFilteredRestaurants(
        @Query("categoryId") categoryId: String,
        @Query("freeship") freeship: Boolean? = null,
        @Query("limit") limit: Int = 10
    ): List<CategoryRestaurantResponse>

    @GET("restaurants")
    suspend fun getRestaurantsByIdsForCollection(
        @Query("ids") ids: String
    ): Response<List<RestaurantResponse>>

    @GET("restaurants/summary")
    suspend fun getRestaurantsByIds(
        @Query("ids") restaurantIds: String
    ): Response<List<RestaurantResponse>>

    @GET("restaurants/{id}")
    suspend fun getRestaurantDetails(
        @Path("id") restaurantId: String
    ): Response<RestaurantDetailsResponse>

    @GET("restaurants/{id}/vouchers")
    suspend fun getUserRestaurantVouchers(
        @Path("id") restaurantId: String,
        @Query("userId") userId: String? = null
    ): Response<List<RestaurantVoucherResponse>>

    @GET("restaurants/{restaurantId}/combos")
    suspend fun getRestaurantCombos(
        @Path("restaurantId") restaurantId: String,
        @Query("limit") limit: Int = 3
    ): Response<List<ComboResponse>>

    @GET("restaurants/{restaurantId}/menu-items")
    suspend fun getRestaurantMenuItems(
        @Path("restaurantId") restaurantId: String
    ): Response<List<MenuItemResponse>>

    @GET("restaurants/{restaurantId}/menu-items/for-you")
    suspend fun getForYouMenuItems(
        @Path("restaurantId") restaurantId: String,
        @Query("limit") limit: Int = 4
    ): Response<List<MenuItemResponse>>

    @GET("restaurants/{restaurantId}/reviews")
    suspend fun getRestaurantReviews(
        @Path("restaurantId") restaurantId: String,
        @Query("limit") limit: Int = 10
    ): Response<List<RestaurantReviewResponse>>

    // ---------- Menu items ----------

    @GET("menu-items/search")
    suspend fun searchMenuItemsWithRestaurants(
        @Query("name") nameQuery: String,
        @Query("limit") limit: Int = 20
    ): List<MenuItemWithRestaurantResponse>

    @GET("menu-items/by-category")
    suspend fun getMenuItemsByCategory(
        @Query("categoryId") categoryId: String,
        @Query("limit") limit: Int = 6
    ): List<CategoryMenuItemResponse>

    @GET("menu-items/{id}")
    suspend fun getFoodDetails(
        @Path("id") foodItemId: String
    ): FoodDetailsResponse

    @GET("menu-items/{id}/customization-options")
    suspend fun getCustomizationOptions(
        @Path("id") menuItemId: String
    ): FoodCustomizationResponse

    // ---------- Users ----------

    @GET("users")
    suspend fun getUser(
        @Query("id") userId: String? = null,
        @Query("email") email: String? = null
    ): Response<List<UserResponse>>

    @PATCH("users/{id}")
    suspend fun updateUser(
        @Path("id") userId: String,
        @Body updateRequest: UpdateProfileRequest
    ): Response<List<UserResponse>>

    @GET("users/{id}/addresses")
    suspend fun getUserAddresses(
        @Path("id") userId: String
    ): Response<List<UserAddress>>

    @GET("users/{id}/favorites")
    suspend fun getUserFavorites(
        @Path("id") userId: String
    ): Response<List<FavoriteResponse>>

    @GET("users/{id}/favorites/restaurants")
    suspend fun getFavoriteRestaurantsWithDetails(
        @Path("id") userId: String
    ): Response<List<FavoriteWithRestaurantResponse>>

    @GET("users/{id}/favorites/menu-items")
    suspend fun getFavoriteMenuItemsWithDetails(
        @Path("id") userId: String
    ): Response<List<FavoriteWithMenuItemResponse>>

    @GET("users/{id}/orders")
    suspend fun getUserOrders(
        @Path("id") userId: String,
        @Query("status") status: String? = null
    ): Response<List<Order>>

    @GET("users/{id}/orders")
    suspend fun getUserOrdersByStatus(
        @Path("id") userId: String,
        @Query("status") status: String
    ): Response<List<Order>>

    @GET("users/{id}/orders/active")
    suspend fun getActiveUserOrders(
        @Path("id") userId: String
    ): Response<List<Order>>

    @GET("users/{id}/vouchers")
    suspend fun getUserVouchers(
        @Path("id") userId: String
    ): Response<List<VoucherApiResponse>>

    @GET("users/{id}/vouchers/active")
    suspend fun getActiveVouchers(
        @Path("id") userId: String
    ): Response<List<VoucherApiResponse>>

    @GET("users/{id}/vouchers/used")
    suspend fun getUsedVouchers(
        @Path("id") userId: String
    ): Response<List<VoucherApiResponse>>

    @GET("users/{id}/restaurants/{restaurantId}/vouchers")
    suspend fun getRestaurantVouchers(
        @Path("id") userId: String,
        @Path("restaurantId") restaurantId: String
    ): Response<List<VoucherApiResponse>>

    // ---------- Devices / Push ----------

    @POST("devices")
    suspend fun registerDevice(
        @Body request: com.app.tastybuds.data.model.DeviceRegistrationRequest
    ): Response<Unit>

    // ---------- Favorites ----------

    @POST("favorites")
    suspend fun addFavorite(
        @Body favoriteRequest: AddFavoriteRequest
    ): Response<List<FavoriteResponse>>

    @DELETE("favorites")
    suspend fun removeFavorite(
        @Query("userId") userId: String,
        @Query("menuItemId") menuItemId: String? = null,
        @Query("restaurantId") restaurantId: String? = null
    ): Response<List<FavoriteResponse>>

    // ---------- Orders ----------

    @POST("orders")
    suspend fun createOrder(
        @Body orderRequest: CreateOrderRequest
    ): Response<List<Order>>

    @GET("orders/{id}")
    suspend fun getOrderById(
        @Path("id") orderId: String
    ): Response<List<Order>>

    @PATCH("orders/{id}/status")
    suspend fun updateOrderStatusExt(
        @Path("id") orderId: String,
        @Body statusUpdate: UpdateOrderStatusRequest
    ): Response<List<Order>>

    // ---------- Vouchers ----------

    @GET("vouchers")
    suspend fun getGlobalVouchers(
        @Query("userId") userId: String? = null,
        @Query("unusedOnly") unusedOnly: Boolean = true
    ): Response<List<Voucher>>

    @PATCH("vouchers/{id}/use")
    suspend fun markVoucherAsUsed(
        @Path("id") voucherId: String,
        @Body voucherUsage: VoucherUsageRequest
    ): Response<List<VoucherApiResponse>>
}
