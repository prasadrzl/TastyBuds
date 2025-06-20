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
import com.app.tastybuds.data.model.UserAddress
import com.app.tastybuds.data.model.Voucher
import com.app.tastybuds.data.model.VoucherCountResponse
import com.app.tastybuds.domain.model.UpdateProfileRequest
import com.app.tastybuds.domain.model.UserResponse
import com.app.tastybuds.ui.resturants.FoodCustomizationResponse
import com.app.tastybuds.ui.resturants.FoodDetailsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
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

    @GET("menu_items")
    suspend fun searchMenuItemsWithRestaurants(
        @Query("name") nameQuery: String? = null,
        @Query("select") select: String = "*,restaurants(*)",
        @Query("order") order: String = "price.asc",
        @Query("limit") limit: Int = 20
    ): List<MenuItemWithRestaurantResponse>

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

    @GET("menu_items")
    suspend fun getRestaurantMenuItems(
        @Query("restaurant_id") restaurantId: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "is_popular.desc,rating.desc"
    ): Response<List<MenuItemResponse>>

    @GET("menu_items")
    suspend fun getForYouMenuItems(
        @Query("restaurant_id") restaurantId: String,
        @Query("is_popular") isPopular: String = "eq.true",
        @Query("select") select: String = "*",
        @Query("limit") limit: Int = 4
    ): Response<List<MenuItemResponse>>

    @GET("restaurant_reviews_with_time")
    suspend fun getRestaurantReviews(
        @Query("restaurant_id") restaurantId: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc",
        @Query("limit") limit: Int = 10
    ): Response<List<RestaurantReviewResponse>>

    @GET("active_restaurant_vouchers")
    suspend fun getRestaurantVouchers(
        @Query("restaurant_id") restaurantId: String,
        @Query("select") select: String = "*"
    ): Response<List<RestaurantVoucherResponse>>

    @GET("active_restaurant_vouchers")
    suspend fun getUserRestaurantVouchers(
        @Query("user_id") userId: String,
        @Query("restaurant_id") restaurantId: String,
        @Query("select") select: String = "*"
    ): Response<List<RestaurantVoucherResponse>>

    @GET("combos")
    suspend fun getRestaurantCombos(
        @Query("restaurant_id") restaurantId: String,
        @Query("select") select: String = "*"
    ): Response<List<ComboResponse>>

    @GET("restaurants")
    suspend fun getRestaurantDetails(
        @Query("id") restaurantId: String,
        @Query("select") select: String = "*"
    ): Response<List<RestaurantDetailsResponse>>

    @GET("menu_items")
    suspend fun getFoodDetails(
        @Query("id") foodItemId: String,
        @Query("select") select: String = "*"
    ): List<FoodDetailsResponse>

    @GET("customization_options")
    suspend fun getCustomizationOptions(
        @Query("menu_item_id") menuItemId: String,
        @Query("select") select: String = "*"
    ): List<FoodCustomizationResponse>

    @GET("users")
    suspend fun getUser(
        @Query("id") userId: String? = null,
        @Query("email") email: String? = null,
        @Query("select") select: String = "*"
    ): Response<List<UserResponse>>

    @PATCH("users")
    suspend fun updateUser(
        @Query("id") userId: String,
        @Body updateRequest: UpdateProfileRequest
    ): Response<List<UserResponse>>

    @GET("favorites")
    suspend fun getUserFavorites(
        @Query("user_id") userId: String,
        @Query("select") select: String = "*"
    ): Response<List<FavoriteResponse>>

    @POST("favorites")
    suspend fun addFavorite(
        @Body favoriteRequest: AddFavoriteRequest
    ): Response<List<FavoriteResponse>>

    @DELETE("favorites")
    suspend fun removeFavorite(
        @Query("user_id") userId: String,
        @Query("menu_item_id") menuItemId: String? = null,
        @Query("restaurant_id") restaurantId: String? = null
    ): Response<List<FavoriteResponse>>

    @GET("favorites")
    suspend fun getFavoriteRestaurantsWithDetails(
        @Query("user_id") userId: String,
        @Query("restaurant_id") restaurantFilter: String = "not.is.null",
        @Query("select") select: String = "*,restaurants(*)"
    ): Response<List<FavoriteWithRestaurantResponse>>

    @GET("favorites")
    suspend fun getFavoriteMenuItemsWithDetails(
        @Query("user_id") userId: String,
        @Query("menu_item_id") menuItemFilter: String = "not.is.null",
        @Query("select") select: String = "*,menu_items(*,restaurants(name,id))"
    ): Response<List<FavoriteWithMenuItemResponse>>

    @GET("user_addresses")
    suspend fun getUserAddresses(
        @Query("user_id") userId: String? = null,
        @Query("select") select: String = "*"
    ): Response<List<UserAddress>>

    @POST("orders")
    suspend fun createOrder(
        @Body orderRequest: CreateOrderRequest
    ): Response<List<Order>>

    @GET("orders")
    suspend fun getUserOrders(
        @Query("user_id") userId: String,
        @Query("order") order: String = "created_at.desc",
        @Query("select") select: String = "*"
    ): Response<List<Order>>

    @GET("orders")
    suspend fun getOrderById(
        @Query("id") orderId: String,
        @Query("select") select: String = "*"
    ): Response<List<Order>>

    @PATCH("orders")
    suspend fun updateOrderStatus(
        @Query("id") orderId: String,
        @Body statusUpdate: Map<String, String>
    ): Response<List<Order>>

    @GET("vouchers")
    suspend fun getGlobalVouchers(
        @Query("user_id") userId: String? = null,
        @Query("is_used") isUsed: String = "eq.false",
        @Query("select") select: String = "*"
    ): Response<List<Voucher>>
}