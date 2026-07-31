package com.app.tastybuds.data.model.mapper

import com.app.tastybuds.data.model.FavoriteWithMenuItemResponse
import com.app.tastybuds.data.model.FavoriteWithRestaurantResponse
import com.app.tastybuds.data.model.MenuItemWithRestaurantResponse
import com.app.tastybuds.data.model.RestaurantDetailsResponse
import com.app.tastybuds.data.model.RestaurantResponse
import org.junit.Assert.assertEquals
import org.junit.Test

// Field-by-field DTO -> domain mapping coverage. This used to be verified indirectly
// through FavoritesUseCase before the mapping moved into the data layer
// (FavoritesRepositoryImpl), where it now belongs per Clean Architecture.
class FavoriteResponseMapperTest {

    @Test
    fun `FavoriteWithRestaurantResponse toDomain maps every field`() {
        val response = FavoriteWithRestaurantResponse(
            id = 1,
            restaurantId = "restaurant_789",
            createdAt = "2024-01-01",
            restaurant = RestaurantDetailsResponse(
                name = "Test Restaurant",
                cuisine = listOf("Italian", "Pizza"),
                rating = 4.5f,
                reviewCount = 100,
                deliveryTime = "30-45 min",
                distance = "2.5 km",
                priceRange = "$$",
                imageUrl = "https://test.com/restaurant.jpg",
                deliveryFee = 3.99f,
                isOpen = true
            )
        )

        val domain = response.toDomain()

        assertEquals(1, domain.id)
        assertEquals("restaurant_789", domain.restaurantId)
        assertEquals("Test Restaurant", domain.name)
        assertEquals("Italian, Pizza", domain.cuisine)
        assertEquals(4.5f, domain.rating)
        assertEquals(100, domain.reviewCount)
        assertEquals("30-45 min", domain.deliveryTime)
        assertEquals("2.5 km", domain.distance)
        assertEquals("$$", domain.priceRange)
        assertEquals("https://test.com/restaurant.jpg", domain.imageUrl)
        assertEquals(3.99f, domain.deliveryFee)
        assertEquals(true, domain.isOpen)
        assertEquals("2024-01-01", domain.createdAt)
    }

    @Test
    fun `FavoriteWithRestaurantResponse toDomain falls back to defaults when restaurant is null`() {
        val domain = FavoriteWithRestaurantResponse(id = 1, createdAt = "2024-01-01").toDomain()

        assertEquals("", domain.restaurantId)
        assertEquals("", domain.name)
        assertEquals(true, domain.isOpen)
    }

    @Test
    fun `FavoriteWithMenuItemResponse toDomain maps every field`() {
        val response = FavoriteWithMenuItemResponse(
            id = 2,
            menuItemId = "menu_item_456",
            createdAt = "2024-01-01",
            menuItem = MenuItemWithRestaurantResponse(
                name = "Test Pizza",
                description = "Delicious test pizza",
                price = 15.99,
                image = "https://test.com/pizza.jpg",
                rating = 4.2f,
                reviewCount = 50,
                isPopular = true,
                isSpicy = false,
                restaurant = RestaurantResponse(id = "restaurant_789", name = "Test Restaurant")
            )
        )

        val domain = response.toDomain()

        assertEquals(2, domain.id)
        assertEquals("menu_item_456", domain.menuItemId)
        assertEquals("Test Pizza", domain.name)
        assertEquals("Delicious test pizza", domain.description)
        assertEquals(15.99f, domain.price)
        assertEquals("https://test.com/pizza.jpg", domain.imageUrl)
        assertEquals(4.2f, domain.rating)
        assertEquals(50, domain.reviewCount)
        assertEquals("Test Restaurant", domain.restaurantName)
        assertEquals("restaurant_789", domain.restaurantId)
        assertEquals(true, domain.isPopular)
        assertEquals(false, domain.isSpicy)
        assertEquals("2024-01-01", domain.createdAt)
    }

    @Test
    fun `FavoriteWithMenuItemResponse toDomain falls back to defaults when menuItem is null`() {
        val domain = FavoriteWithMenuItemResponse(id = 2, createdAt = "2024-01-01").toDomain()

        assertEquals("", domain.name)
        assertEquals(0.0f, domain.price)
        assertEquals(false, domain.isPopular)
    }
}
