package com.app.tastybuds.domain

import com.app.tastybuds.domain.repository.HomeRepository
import com.app.tastybuds.domain.model.Banner
import com.app.tastybuds.domain.model.Category
import com.app.tastybuds.domain.model.Deal
import com.app.tastybuds.domain.model.Restaurant
import com.app.tastybuds.util.Result
import javax.inject.Inject
import com.app.tastybuds.domain.model.Collection as FoodCollection

class HomeUseCase @Inject constructor(private val repository: HomeRepository) {

    // Each section degrades independently to an empty/zero fallback on failure — a home
    // screen combines several unrelated widgets, so one failed section (e.g. deals)
    // shouldn't block the rest of the screen from showing. Failures are still logged
    // (not silently swallowed like the old Flow + .catch{ emit(emptyList()) } did).
    suspend fun getHomeData(userId: String = "user_001"): HomeData {
        val banners = repository.getBanners().orLogEmpty("banners")
        val categories = repository.getCategories().orLogEmpty("categories")
        val voucherCount = repository.getVoucherCount(userId).orLogDefault("voucherCount", 0)
        val collections = repository.getCollections().orLogEmpty("collections")
        val restaurants = repository.getRecommendedRestaurants().orLogEmpty("recommendedRestaurants")
        val deals = repository.getDeals().orLogEmpty("deals")

        return HomeData(
            banners = banners,
            categories = categories,
            voucherCount = voucherCount,
            collections = collections,
            recommendedRestaurants = restaurants,
            deals = deals
        )
    }

    private fun <T> Result<List<T>>.orLogEmpty(section: String): List<T> =
        orLogDefault(section, emptyList())

    private fun <T> Result<T>.orLogDefault(section: String, default: T): T = when (this) {
        is Result.Success -> data
        is Result.Error -> {
            println("HomeUseCase: failed to load $section: $message")
            default
        }

        is Result.Loading -> default
    }
}

data class HomeData(
    val banners: List<Banner>,
    val categories: List<Category>,
    val voucherCount: Int,
    val collections: List<FoodCollection>,
    val recommendedRestaurants: List<Restaurant>,
    val deals: List<Deal>
)