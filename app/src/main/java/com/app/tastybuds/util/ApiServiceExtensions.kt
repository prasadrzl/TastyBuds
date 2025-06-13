package com.app.tastybuds.util

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.RestaurantResponse

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

suspend fun TastyBudsApiService.getTopRatedItems(
    categoryId: String,
    limit: Int = 10
) = getMenuItemsByCategory(
    categoryId = "eq.$categoryId",
    isPopular = "eq.true",
    order = "rating.desc",
    limit = limit
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

private fun getDateDaysAgo(days: Int): String {
    val calendar = java.util.Calendar.getInstance()
    calendar.add(java.util.Calendar.DAY_OF_YEAR, -days)
    return java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        .format(calendar.time)
}

suspend fun TastyBudsApiService.searchRestaurantsEnhanced(
    query: String,
    sortBy: String = "rating.desc",
    limit: Int = 20
) = searchRestaurants(
    nameQuery = "ilike.*$query*",
    select = "*"
).sortedWith(
    when (sortBy) {
        "rating.desc" -> compareByDescending { it.rating }
        "delivery_time.asc" -> compareBy { parseDeliveryTime(it.deliveryTime) }
        "distance.asc" -> compareBy { parseDistance(it.distance) }
        else -> compareByDescending { it.rating }
    }
).take(limit)

suspend fun TastyBudsApiService.searchRestaurantsMultiField(
    query: String,
    includeDescription: Boolean = true,
    includeCuisine: Boolean = true
): List<RestaurantResponse> {
    val searchQuery = buildString {
        append("name.ilike.*$query*")
        if (includeDescription) append(",description.ilike.*$query*")
        if (includeCuisine) append(",cuisine.cs.{$query}")
    }

    return searchRestaurants(nameQuery = searchQuery)
}