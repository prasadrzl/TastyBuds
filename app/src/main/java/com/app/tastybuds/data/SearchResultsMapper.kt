package com.app.tastybuds.data

import com.app.tastybuds.data.model.MenuItemWithRestaurantResponse
import com.app.tastybuds.data.model.RestaurantResponse
import javax.inject.Inject
import kotlin.text.*

class SearchResultsMapper @Inject constructor() {

    fun mapToSearchResults(
        menuItemsResponse: List<MenuItemWithRestaurantResponse>
    ): List<SearchResult> {
        // Group menu items by restaurant
        val groupedByRestaurant = menuItemsResponse
            .filter { it.restaurant != null }
            .groupBy { it.restaurant!! }

        // Convert to SearchResult list
        return groupedByRestaurant.map { (restaurantResponse, menuItems) ->
            SearchResult(
                id = restaurantResponse.id,
                restaurant = mapToRestaurant(restaurantResponse),
                menuItemList = menuItems.map { mapToMenuItem(it) }
            )
        }.sortedByDescending { it.restaurant?.rating ?: 0f }
    }

    private fun mapToRestaurant(restaurantResponse: RestaurantResponse): Restaurant {
        return Restaurant(
            id = restaurantResponse.id,
            name = restaurantResponse.name,
            description = restaurantResponse.description,
            cuisine = restaurantResponse.cuisine,
            rating = restaurantResponse.rating,
            reviewCount = restaurantResponse.reviewCount,
            deliveryTime = restaurantResponse.deliveryTime,
            distance = restaurantResponse.distance,
            deliveryFee = restaurantResponse.deliveryFee.toDouble(),
            imageUrl = restaurantResponse.imageUrl,
            badges = restaurantResponse.badges.map { it.text },
            isOpen = restaurantResponse.isOpen,
            isFavorite = restaurantResponse.isFavorite,
            priceRange = restaurantResponse.priceRange,
            type = SearchResultType.RESTAURANT
        )
    }

    private fun mapToMenuItem(menuItemResponse: MenuItemWithRestaurantResponse): MenuItem {
        return MenuItem(
            id = menuItemResponse.id,
            name = menuItemResponse.name,
            description = menuItemResponse.description,
            price = menuItemResponse.price,
            imageUrl = menuItemResponse.image,
            rating = menuItemResponse.rating,
            reviewCount = menuItemResponse.reviewCount,
            isPopular = menuItemResponse.isPopular,
            isSpicy = menuItemResponse.isSpicy,
            prepTime = menuItemResponse.prepTime ?: "15-20 mins",
            categoryId = "",
            type = SearchResultType.FOOD_ITEM
        )
    }
}