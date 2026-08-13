package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.core.data.util.ErrorHandler
import com.app.tastybuds.data.model.mapper.toDeal
import com.app.tastybuds.data.model.mapper.toDomainModel
import com.app.tastybuds.domain.model.Banner
import com.app.tastybuds.domain.model.Category
import com.app.tastybuds.domain.model.Deal
import com.app.tastybuds.domain.model.Restaurant
import com.app.tastybuds.domain.repository.HomeRepository
import com.app.tastybuds.util.Result
import javax.inject.Inject
import com.app.tastybuds.domain.model.Collection as FoodCollection

/**
 * Offline-capable Home repository. Each section fetches from the network and caches the result;
 * on a network failure it falls back to the last cached copy so Home always renders. Only a first
 * launch with no cache and no network yields an error.
 */
class HomeRepositoryImpl @Inject constructor(
    private val tastyBudsApiService: TastyBudsApiService,
    private val cache: HomeCache
) : HomeRepository {

    override suspend fun getBanners(): Result<List<Banner>> = networkThenCache(
        key = KEY_BANNERS, elementType = Banner::class.java
    ) { tastyBudsApiService.getBanners().map { it.toDomainModel() } }

    override suspend fun getCategories(): Result<List<Category>> = networkThenCache(
        key = KEY_CATEGORIES, elementType = Category::class.java
    ) { tastyBudsApiService.getCategories().map { it.toDomainModel() } }

    override suspend fun getCollections(): Result<List<FoodCollection>> = networkThenCache(
        key = KEY_COLLECTIONS, elementType = FoodCollection::class.java
    ) { tastyBudsApiService.getCollections().map { it.toDomainModel() } }

    override suspend fun getRecommendedRestaurants(): Result<List<Restaurant>> = networkThenCache(
        key = KEY_RECOMMENDED, elementType = Restaurant::class.java
    ) { tastyBudsApiService.getRecommendedRestaurants().map { it.toDomainModel() } }

    override suspend fun getDeals(): Result<List<Deal>> = networkThenCache(
        key = KEY_DEALS, elementType = Deal::class.java
    ) { tastyBudsApiService.getDeals().map { it.toDeal() } }

    override suspend fun getVoucherCount(userId: String): Result<Int> {
        return try {
            val count = tastyBudsApiService.getVoucherCount(userId).count { !it.isUsed }
            cache.saveList(KEY_VOUCHER_COUNT, listOf(count.toString()), String::class.java)
            Result.Success(count)
        } catch (e: Exception) {
            val cached = cache.loadList(KEY_VOUCHER_COUNT, String::class.java)?.firstOrNull()?.toIntOrNull()
            if (cached != null) Result.Success(cached) else Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    private suspend fun <T> networkThenCache(
        key: String,
        elementType: Class<T>,
        fetch: suspend () -> List<T>
    ): Result<List<T>> {
        return try {
            val data = fetch()
            cache.saveList(key, data, elementType)
            Result.Success(data)
        } catch (e: Exception) {
            val cached = cache.loadList(key, elementType)
            if (!cached.isNullOrEmpty()) Result.Success(cached)
            else Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    private companion object {
        const val KEY_BANNERS = "home_banners"
        const val KEY_CATEGORIES = "home_categories"
        const val KEY_COLLECTIONS = "home_collections"
        const val KEY_RECOMMENDED = "home_recommended"
        const val KEY_DEALS = "home_deals"
        const val KEY_VOUCHER_COUNT = "home_voucher_count"
    }
}
