package com.app.tastybuds.data.repo

import com.app.tastybuds.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import androidx.compose.ui.graphics.Color
import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.domain.model.Banner
import com.app.tastybuds.domain.model.Category
import com.app.tastybuds.domain.model.Deal
import com.app.tastybuds.domain.model.Restaurant
import com.app.tastybuds.domain.model.Collection as FoodCollection
import androidx.core.graphics.toColorInt

interface HomeRepository {
    fun getBanners(): Flow<List<Banner>>
    fun getCategories(): Flow<List<Category>>
    fun getVoucherCount(userId: String): Flow<Int>
    fun getCollections(): Flow<List<FoodCollection>>
    fun getRecommendedRestaurants(): Flow<List<Restaurant>>
    fun getDeals(): Flow<List<Deal>>
    fun searchRestaurants(query: String): Flow<List<Restaurant>>
}

class HomeRepositoryImpl @Inject constructor(
    private val tastyBudsApiService: TastyBudsApiService
) : HomeRepository {

    override fun getBanners(): Flow<List<Banner>> = flow {
        try {
            val response = tastyBudsApiService.getBanners()
            val banners = response.map { it.toDomainModel() }
            emit(banners)
        } catch (e: Exception) {
            // In a real app, we would handle errors more gracefully
            emit(emptyList())
        }
    }

    override fun getCategories(): Flow<List<Category>> = flow {
        try {
            val response = tastyBudsApiService.getCategories()
            val categories = response.map { it.toDomainModel() }
            emit(categories)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getVoucherCount(userId: String): Flow<Int> = flow {
        try {
            val response = tastyBudsApiService.getVoucherCount(userId)
            val count = response.firstOrNull()?.voucherCount ?: 0
            emit(count)
        } catch (e: Exception) {
            emit(0)
        }
    }

    override fun getCollections(): Flow<List<FoodCollection>> = flow {
        try {
            val response = tastyBudsApiService.getCollections()
            val collections = response.map { it.toDomainModel() }
            emit(collections)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getRecommendedRestaurants(): Flow<List<Restaurant>> = flow {
        try {
            val response = tastyBudsApiService.getRecommendedRestaurants()
            val restaurants = response.map { it.toDomainModel() }
            emit(restaurants)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getDeals(): Flow<List<Deal>> = flow {
        try {
            val response = tastyBudsApiService.getDeals()
            val deals = response.map { it.toDomainModel() }
            emit(deals)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun searchRestaurants(query: String): Flow<List<Restaurant>> = flow {
        try {
            val response = tastyBudsApiService.searchRestaurants("ilike.*$query*")
            val restaurants = response.map { it.toDomainModel() }
            emit(restaurants)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Extension functions to map API responses to domain models
    private fun BannerResponse.toDomainModel(): Banner {
        return Banner(
            id = id,
            title = title,
            price = price,
            description = description,
            imageUrl = imageUrl,
            backgroundColor = parseColor(colorHex)
        )
    }

    private fun CategoryResponse.toDomainModel(): Category {
        return Category(
            id = id,
            name = name,
            imageUrl = image_url,
            backgroundColor = parseColor(colorHex)
        )
    }

    private fun CollectionResponse.toDomainModel(): FoodCollection {
        return FoodCollection(
            id = id,
            title = title,
            subtitle = subtitle,
            imageUrl = imageUrl,
            badge = badge
        )
    }

    private fun RestaurantResponse.toDomainModel(): Restaurant {
        return Restaurant(
            id = id,
            name = name,
            cuisine = cuisine,
            rating = rating,
            reviewCount = reviewCount,
            deliveryTime = deliveryTime,
            distance = distance,
            imageUrl = imageUrl,
            badge = badge
        )
    }

    private fun DealResponse.toDomainModel(): Deal {
        return Deal(
            id = id,
            title = title,
            price = price,
            originalPrice = originalPrice,
            imageUrl = imageUrl,
            badge = badge,
            discountPercentage = discountPercentage
        )
    }

    private fun parseColor(colorHex: String): Color {
        return try {
            Color(colorHex.toColorInt())
        } catch (e: Exception) {
            Color(0xFFFF7700)
        }
    }
}