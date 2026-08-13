package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.BannerResponse
import com.app.tastybuds.data.model.VoucherCountResponse
import com.app.tastybuds.domain.model.Banner
import com.app.tastybuds.util.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeRepositoryTest {

    private lateinit var api: TastyBudsApiService
    private lateinit var cache: HomeCache
    private lateinit var repository: HomeRepositoryImpl

    @Before
    fun setUp() {
        api = mockk()
        cache = mockk(relaxed = true)
        repository = HomeRepositoryImpl(api, cache)
    }

    @Test
    fun `getBanners returns network data and writes it to the cache`() = runTest {
        coEvery { api.getBanners() } returns listOf(BannerResponse(id = "b1"), BannerResponse(id = "b2"))

        val result = repository.getBanners()

        assertTrue(result is Result.Success)
        assertEquals(2, (result as Result.Success).data.size)
        coVerify { cache.saveList(any(), any<List<Banner>>(), Banner::class.java) }
    }

    @Test
    fun `getBanners serves cached data when the network fails`() = runTest {
        coEvery { api.getBanners() } throws RuntimeException("offline")
        coEvery { cache.loadList(any(), Banner::class.java) } returns listOf(Banner(id = "cached"))

        val result = repository.getBanners()

        assertTrue(result is Result.Success)
        assertEquals("cached", (result as Result.Success).data.first().id)
    }

    @Test
    fun `getBanners returns Error when the network fails and the cache is empty`() = runTest {
        coEvery { api.getBanners() } throws RuntimeException("offline")
        coEvery { cache.loadList(any(), Banner::class.java) } returns null

        assertTrue(repository.getBanners() is Result.Error)
    }

    @Test
    fun `getVoucherCount counts only the unused vouchers and caches the value`() = runTest {
        coEvery { api.getVoucherCount("u1") } returns listOf(
            VoucherCountResponse(isUsed = false),
            VoucherCountResponse(isUsed = false),
            VoucherCountResponse(isUsed = true)
        )

        val result = repository.getVoucherCount("u1")

        assertEquals(2, (result as Result.Success).data)
        coVerify { cache.saveList(any(), listOf("2"), String::class.java) }
    }

    @Test
    fun `getVoucherCount falls back to the cached count when the network fails`() = runTest {
        coEvery { api.getVoucherCount("u1") } throws RuntimeException("offline")
        coEvery { cache.loadList(any(), String::class.java) } returns listOf("5")

        assertEquals(5, (repository.getVoucherCount("u1") as Result.Success).data)
    }
}
