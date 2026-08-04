package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.RestaurantResponse
import com.app.tastybuds.data.model.VoucherApiResponse
import com.app.tastybuds.util.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class VouchersRepositoryImplTest {

    private lateinit var apiService: TastyBudsApiService
    private lateinit var repository: VouchersRepositoryImpl

    @Before
    fun setUp() {
        apiService = mockk()
        repository = VouchersRepositoryImpl(apiService)
        // fetchRestaurantNames() is called for any voucher carrying a restaurant id.
        coEvery { apiService.getRestaurantsByIds(any()) } returns
                Response.success(listOf(RestaurantResponse(id = "r1", name = "Pizza Place")))
    }

    // A voucher that carries a restaurant id so the name-resolution + mapping paths run.
    private fun voucherDto() = VoucherApiResponse(
        id = "v1", userId = "u1", title = "10% off", value = 10.0,
        restaurantId = "r1", discountType = "percentage", isActive = true,
        minimumOrderAmount = 0.0, usageLimit = 1, usedCount = 0
    )

    private fun okEmpty() = Response.success(emptyList<VoucherApiResponse>())
    private fun okWith() = Response.success(listOf(voucherDto()))
    private fun <T> httpError() = Response.error<T>(500, "err".toResponseBody(null))

    @Test
    fun `getActiveVouchers returns success with an empty list`() = runTest {
        coEvery { apiService.getActiveVouchers(any()) } returns okEmpty()

        val result = repository.getActiveVouchers("u1")

        assertTrue(result is Result.Success)
        assertEquals(emptyList<Any>(), (result as Result.Success).data)
    }

    @Test
    fun `getActiveVouchers maps a populated body and resolves restaurant names`() = runTest {
        coEvery { apiService.getActiveVouchers(any()) } returns okWith()

        assertTrue(repository.getActiveVouchers("u1") is Result.Success)
    }

    @Test
    fun `getUsedVouchers maps a populated body`() = runTest {
        coEvery { apiService.getUsedVouchers(any()) } returns okWith()
        assertTrue(repository.getUsedVouchers("u1") is Result.Success)
    }

    @Test
    fun `getUserVouchers maps a populated body`() = runTest {
        coEvery { apiService.getUserVouchers(any()) } returns okWith()
        assertTrue(repository.getUserVouchers("u1") is Result.Success)
    }

    @Test
    fun `getRestaurantVouchers maps a populated body`() = runTest {
        coEvery { apiService.getRestaurantVouchers(any(), any()) } returns okWith()
        assertTrue(repository.getRestaurantVouchers("u1", "r1") is Result.Success)
    }

    @Test
    fun `markVoucherAsUsed returns the updated voucher on success`() = runTest {
        coEvery { apiService.markVoucherAsUsed(any(), any()) } returns okWith()
        assertTrue(repository.markVoucherAsUsed("v1", "o1") is Result.Success)
    }

    @Test
    fun `getActiveVouchers maps an unsuccessful response to an error`() = runTest {
        coEvery { apiService.getActiveVouchers(any()) } returns httpError()

        assertTrue(repository.getActiveVouchers("u1") is Result.Error)
    }

    @Test
    fun `getActiveVouchers maps a thrown exception to an error`() = runTest {
        coEvery { apiService.getActiveVouchers(any()) } throws RuntimeException("network down")

        assertTrue(repository.getActiveVouchers("u1") is Result.Error)
    }

    @Test
    fun `getUsedVouchers returns success on empty body`() = runTest {
        coEvery { apiService.getUsedVouchers(any()) } returns okEmpty()
        assertTrue(repository.getUsedVouchers("u1") is Result.Success)
    }

    @Test
    fun `getUserVouchers returns success on empty body`() = runTest {
        coEvery { apiService.getUserVouchers(any()) } returns okEmpty()
        assertTrue(repository.getUserVouchers("u1") is Result.Success)
    }

    @Test
    fun `refreshVouchers returns success on empty body`() = runTest {
        coEvery { apiService.getUserVouchers(any()) } returns okEmpty()
        assertTrue(repository.refreshVouchers("u1") is Result.Success)
    }

    @Test
    fun `refreshVouchers maps an error response`() = runTest {
        coEvery { apiService.getUserVouchers(any()) } returns httpError()
        assertTrue(repository.refreshVouchers("u1") is Result.Error)
    }

    @Test
    fun `getRestaurantVouchers returns success on empty body`() = runTest {
        coEvery { apiService.getRestaurantVouchers(any(), any()) } returns okEmpty()
        assertTrue(repository.getRestaurantVouchers("u1", "r1") is Result.Success)
    }

    @Test
    fun `markVoucherAsUsed errors when the updated voucher is missing`() = runTest {
        coEvery { apiService.markVoucherAsUsed(any(), any()) } returns okEmpty()
        assertTrue(repository.markVoucherAsUsed("v1", "o1") is Result.Error)
    }

    @Test
    fun `markVoucherAsUsed maps an error response`() = runTest {
        coEvery { apiService.markVoucherAsUsed(any(), any()) } returns httpError()
        assertTrue(repository.markVoucherAsUsed("v1", "o1") is Result.Error)
    }
}
