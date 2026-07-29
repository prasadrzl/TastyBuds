package com.app.tastybuds.core.data.util

import com.app.tastybuds.common.TastyBudsApiService

suspend fun TastyBudsApiService.getPopularMenuItems(
    categoryId: String,
    limit: Int = 6
) = getMenuItemsByCategory(
    categoryId = categoryId,
    limit = limit
)

suspend fun TastyBudsApiService.getUserBy(
    userId: String? = null,
    email: String? = null
) = getUser(
    userId = userId,
    email = email
)

suspend fun TastyBudsApiService.getRestaurantsByCategoryId(
    categoryId: String,
    limit: Int = 20
) = getRestaurantsByCategory(
    categoryId = categoryId
)

suspend fun TastyBudsApiService.searchRestaurantsByName(
    query: String
) = searchRestaurants(
    nameQuery = query
)

suspend fun TastyBudsApiService.getTopCategoryRestaurants(
    categoryId: String,
    limit: Int = 6
) = getTopRestaurantsByCategory(
    categoryId = categoryId,
    limit = limit
)

suspend fun TastyBudsApiService.getRecommendedByCategoryExt(
    categoryId: String,
    limit: Int = 5
) = getRecommendedRestaurantsByCategory(
    categoryId = categoryId,
    limit = limit
)

suspend fun TastyBudsApiService.getUserAddressesExt(
    userId: String
) = getUserAddresses(
    userId = userId
)

suspend fun TastyBudsApiService.getGlobalVouchersExt(
    userId: String? = null
) = getGlobalVouchers(
    userId = userId
)

suspend fun TastyBudsApiService.getOrderByExt(
    orderId: String
) = getOrderById(
    orderId = orderId
)

suspend fun TastyBudsApiService.getUserOrdersExt(
    userId: String
) = getUserOrders(
    userId = userId
)
