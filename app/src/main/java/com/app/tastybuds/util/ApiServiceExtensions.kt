package com.app.tastybuds.util

import com.app.tastybuds.common.TastyBudsApiService

suspend fun TastyBudsApiService.getPopularMenuItems(
    categoryId: String,
    limit: Int = 6
) = getMenuItemsByCategory(
    categoryId = "eq.$categoryId",
    isPopular = "eq.true",
    order = "rating.desc,review_count.desc",
    limit = limit,
    select = "*,restaurants(name,delivery_time,id)"
)

suspend fun TastyBudsApiService.getUserBy(
    userId: String? = null,
    email: String? = null
) = getUser(
    userId = if (userId != null) "eq.$userId" else null,
    email = if (email != null) "eq.$email" else null,
    select = "*"
)

suspend fun TastyBudsApiService.getRestaurantsByCategoryId(
    categoryId: String,
    limit: Int = 20
) = getRestaurantsByCategory(
    categoryFilter = categoryId.toCategoryFilter(),
    order = "rating.desc"
)

suspend fun TastyBudsApiService.searchRestaurantsByName(
    query: String
) = searchRestaurants(
    nameQuery = "ilike.*$query*"
)

suspend fun TastyBudsApiService.getFilteredRestaurants(
    categoryId: String,
    isFreeShip: Boolean? = null
) = getFilteredRestaurants(
    categoryIds = categoryId.toCategoryFilter(),
    isFreeship = if (isFreeShip == true) "eq.true" else null
)

suspend fun TastyBudsApiService.getTopCategoryRestaurants(
    categoryId: String,
    limit: Int = 6
) = getTopRestaurantsByCategory(
    categoryIds = categoryId.toCategoryFilter(),
    limit = limit
)

suspend fun TastyBudsApiService.getRecommendedByCategoryExt(
    categoryId: String,
    limit: Int = 5
) = getRecommendedRestaurantsByCategory(
    categoryIds = categoryId.toCategoryFilter(),
    limit = limit
)

suspend fun TastyBudsApiService.getUserAddressesExt(
    userId: String? = null
) = getUserAddresses(
    userId = if (userId != null) "eq.$userId" else null,
    select = "*"
)

suspend fun TastyBudsApiService.getGlobalVouchersExt(
    userId: String? = null
) = getGlobalVouchers(
    userId = if (userId != null) "eq.$userId" else null,
    select = "*"
)