package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.domain.model.OrderStatus
import com.app.tastybuds.util.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class OrderRepositoryImplTest {

    private lateinit var apiService: TastyBudsApiService
    private lateinit var repository: OrderRepositoryImpl

    @Before
    fun setUp() {
        apiService = mockk()
        repository = OrderRepositoryImpl(apiService)
    }

    private fun errBody() = "err".toResponseBody(null)

    // ---- getUserAddresses (via getUserAddressesExt) ----

    @Test
    fun `getUserAddresses returns success on an empty body`() = runTest {
        coEvery { apiService.getUserAddresses(any()) } returns Response.success(emptyList())
        assertTrue(repository.getUserAddresses("u1") is Result.Success)
    }

    @Test
    fun `getUserAddresses maps an error response`() = runTest {
        coEvery { apiService.getUserAddresses(any()) } returns Response.error(500, errBody())
        assertTrue(repository.getUserAddresses("u1") is Result.Error)
    }

    @Test
    fun `getUserAddresses maps a thrown exception`() = runTest {
        coEvery { apiService.getUserAddresses(any()) } throws RuntimeException("down")
        assertTrue(repository.getUserAddresses("u1") is Result.Error)
    }

    // ---- getUserOrders (via getUserOrdersExt) ----

    @Test
    fun `getUserOrders returns success on an empty body`() = runTest {
        coEvery { apiService.getUserOrders(any()) } returns Response.success(emptyList())
        assertTrue(repository.getUserOrders("u1") is Result.Success)
    }

    @Test
    fun `getUserOrders maps an error response`() = runTest {
        coEvery { apiService.getUserOrders(any()) } returns Response.error(500, errBody())
        assertTrue(repository.getUserOrders("u1") is Result.Error)
    }

    // ---- getOrderById (via getOrderByExt) ----

    @Test
    fun `getOrderById errors when the order is not found`() = runTest {
        coEvery { apiService.getOrderById(any()) } returns Response.success(emptyList())
        assertTrue(repository.getOrderById("o1") is Result.Error)
    }

    @Test
    fun `getOrderById maps an error response`() = runTest {
        coEvery { apiService.getOrderById(any()) } returns Response.error(404, errBody())
        assertTrue(repository.getOrderById("o1") is Result.Error)
    }

    // ---- getAvailableVouchers (via getGlobalVouchersExt) ----

    @Test
    fun `getAvailableVouchers returns success on an empty body`() = runTest {
        coEvery { apiService.getGlobalVouchers(any()) } returns Response.success(emptyList())
        assertTrue(repository.getAvailableVouchers("u1") is Result.Success)
    }

    @Test
    fun `getAvailableVouchers maps an error response`() = runTest {
        coEvery { apiService.getGlobalVouchers(any()) } returns Response.error(500, errBody())
        assertTrue(repository.getAvailableVouchers("u1") is Result.Error)
    }

    // ---- status-filtered fetches ----

    @Test
    fun `getUserOrdersByStatus returns success on an empty body`() = runTest {
        coEvery { apiService.getUserOrdersByStatus(any(), any()) } returns Response.success(emptyList())
        assertTrue(repository.getUserOrdersByStatus("u1", OrderStatus.DELIVERED) is Result.Success)
    }

    @Test
    fun `getActiveUserOrders returns success on an empty body`() = runTest {
        coEvery { apiService.getActiveUserOrders(any()) } returns Response.success(emptyList())
        assertTrue(repository.getActiveUserOrders("u1") is Result.Success)
    }
}
