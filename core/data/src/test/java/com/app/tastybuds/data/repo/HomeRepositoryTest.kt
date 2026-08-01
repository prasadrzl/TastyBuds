package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.BannerResponse
import com.app.tastybuds.data.model.VoucherCountResponse
import com.app.tastybuds.util.Result
import io.mockk.coEvery
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
    private lateinit var repository: HomeRepositoryImpl

    @Before
    fun setUp() {
        api = mockk()
        repository = HomeRepositoryImpl(api)
    }

    @Test
    fun `getBanners maps the API list into a Success result`() = runTest {
        coEvery { api.getBanners() } returns listOf(BannerResponse(id = "b1"), BannerResponse(id = "b2"))

        val result = repository.getBanners()

        assertTrue(result is Result.Success)
        assertEquals(2, (result as Result.Success).data.size)
    }

    @Test
    fun `getBanners returns Error when the API throws`() = runTest {
        coEvery { api.getBanners() } throws RuntimeException("network")

        val result = repository.getBanners()

        assertTrue(result is Result.Error)
    }

    @Test
    fun `getVoucherCount counts only the unused vouchers`() = runTest {
        coEvery { api.getVoucherCount("u1") } returns listOf(
            VoucherCountResponse(isUsed = false),
            VoucherCountResponse(isUsed = false),
            VoucherCountResponse(isUsed = true)
        )

        val result = repository.getVoucherCount("u1")

        assertEquals(2, (result as Result.Success).data)
    }
}
