package com.app.tastybuds.data.model.mapper

import com.app.tastybuds.data.model.MenuItemWithRestaurantResponse
import com.app.tastybuds.data.model.RestaurantResponse
import com.app.tastybuds.domain.model.MenuItem
import com.app.tastybuds.domain.model.SearchRestaurant
import com.app.tastybuds.domain.model.SearchResult
import com.app.tastybuds.domain.model.SearchResultType
import javax.inject.Inject

class SearchResultsMapper @Inject constructor() {

    fun mapToSearchResults(
        menuItemsResponse: List<MenuItemWithRestaurantResponse>
    ): List<SearchResult> {
        val groupedByRestaurant = menuItemsResponse
            .filter { it.restaurant != null }
            .groupBy { it.restaurant!! }

        return groupedByRestaurant.map { (restaurantResponse, menuItems) ->
            SearchResult(
                id = restaurantResponse.id,
                restaurant = mapToRestaurant(restaurantResponse),
                menuItemList = menuItems.map { mapToMenuItem(it) }
            )
        }.sortedByDescending { it.restaurant?.rating ?: 0f }
    }

    private fun mapToRestaurant(restaurantResponse: RestaurantResponse): SearchRestaurant {
        return SearchRestaurant(
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