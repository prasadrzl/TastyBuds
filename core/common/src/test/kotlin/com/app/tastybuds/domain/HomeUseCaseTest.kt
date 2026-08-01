package com.app.tastybuds.domain

import com.app.tastybuds.domain.model.Banner
import com.app.tastybuds.domain.model.Category
import com.app.tastybuds.domain.model.Deal
import com.app.tastybuds.domain.model.Restaurant
import com.app.tastybuds.domain.repository.HomeRepository
import com.app.tastybuds.util.Result
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import com.app.tastybuds.domain.model.Collection as FoodCollection

@OptIn(ExperimentalCoroutinesApi::class)
class HomeUseCaseTest {

    @MockK
    private lateinit var repository: HomeRepository

    private lateinit var useCase: HomeUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = HomeUseCase(repository)
        // sensible defaults; individual tests override what they care about
        coEvery { repository.getBanners() } returns Result.Success(listOf(mockk<Banner>()))
        coEvery { repository.getCategories() } returns Result.Success(listOf(mockk<Category>(), mockk()))
        coEvery { repository.getVoucherCount(any()) } returns Result.Success(5)
        coEvery { repository.getCollections() } returns Result.Success(listOf(mockk<FoodCollection>()))
        coEvery { repository.getRecommendedRestaurants() } returns Result.Success(listOf(mockk<Restaurant>()))
        coEvery { repository.getDeals() } returns Result.Success(listOf(mockk<Deal>()))
    }

    @Test
    fun `getHomeData aggregates all successful sections`() = runTest {
        val data = useCase.getHomeData("user_1")

        assertEquals(1, data.banners.size)
        assertEquals(2, data.categories.size)
        assertEquals(5, data.voucherCount)
        assertEquals(1, data.collections.size)
        assertEquals(1, data.recommendedRestaurants.size)
        assertEquals(1, data.deals.size)
    }

    @Test
    fun `getHomeData degrades a failing section to empty without failing the whole screen`() = runTest {
        coEvery { repository.getDeals() } returns Result.Error("deals down")

        val data = useCase.getHomeData("user_1")

        assertTrue(data.deals.isEmpty())        // failed section falls back to empty
        assertEquals(1, data.banners.size)      // other sections still load
        assertEquals(5, data.voucherCount)
    }

    @Test
    fun `getHomeData falls back voucherCount to zero on error`() = runTest {
        coEvery { repository.getVoucherCount(any()) } returns Result.Error("no vouchers")

        val data = useCase.getHomeData("user_1")

        assertEquals(0, data.voucherCount)
    }
}
