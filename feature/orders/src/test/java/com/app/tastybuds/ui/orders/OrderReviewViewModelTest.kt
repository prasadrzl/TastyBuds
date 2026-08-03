package com.app.tastybuds.ui.orders

import com.app.tastybuds.domain.OrderUseCase
import com.app.tastybuds.domain.model.CartItem
import com.app.tastybuds.domain.model.Order
import com.app.tastybuds.domain.model.UserAddress
import com.app.tastybuds.domain.repository.AuthRepository
import com.app.tastybuds.util.Result
import com.app.tastybuds.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrderReviewViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var orderUseCase: OrderUseCase

    @MockK
    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() = MockKAnnotations.init(this)

    private fun build() = OrderReviewViewModel(orderUseCase, authRepository)

    private fun cartItem(id: String = "m1", qty: Int = 1) = CartItem(
        menuItemId = id, name = id, image = null, basePrice = 10.0,
        selectedSize = null, selectedToppings = emptyList(), selectedSpiceLevel = null,
        quantity = qty, notes = null, restaurantId = "r1"
    )

    @Test
    fun `loadOrderReviewData reports when the user is not logged in`() = runTest {
        every { authRepository.getUserId() } returns flowOf(null)
        val viewModel = build()

        viewModel.loadOrderReviewData(listOf(cartItem()))

        assertEquals("User not logged in", viewModel.uiState.value.error)
    }

    @Test
    fun `createOrder rejects an empty cart`() = runTest {
        every { authRepository.getUserId() } returns flowOf("u1")
        val viewModel = build()

        viewModel.createOrder()

        assertEquals("Cart is empty", viewModel.uiState.value.error)
    }

    @Test
    fun `createOrder requires a delivery address`() = runTest {
        every { authRepository.getUserId() } returns flowOf(null)
        val viewModel = build()
        viewModel.loadOrderReviewData(listOf(cartItem()))  // sets cart but no address (no user)

        viewModel.createOrder()

        assertEquals("Please select a delivery address", viewModel.uiState.value.error)
    }

    @Test
    fun `createOrder succeeds and exposes the new order id`() = runTest {
        every { authRepository.getUserId() } returns flowOf("u1")
        val address = mockk<UserAddress>(relaxed = true) { every { isDefault } returns true }
        coEvery { orderUseCase.getUserAddresses("u1") } returns Result.Success(listOf(address))
        coEvery { orderUseCase.getAvailableVouchers("u1") } returns Result.Success(emptyList())
        val created = mockk<Order> { every { id } returns "ORD1" }
        coEvery { orderUseCase.createOrder(any(), any(), any(), any(), any()) } returns Result.Success(created)

        val viewModel = build()
        viewModel.loadOrderReviewData(listOf(cartItem()))
        viewModel.createOrder("no onions")

        val state = viewModel.uiState.value
        assertTrue(state.orderCreated)
        assertEquals("ORD1", state.createdOrderId)
    }

    @Test
    fun `removeItem drops the matching cart item`() = runTest {
        every { authRepository.getUserId() } returns flowOf(null)
        val viewModel = build()
        viewModel.loadOrderReviewData(listOf(cartItem("m1"), cartItem("m2")))

        viewModel.removeItem(cartItem("m1"))

        val items = viewModel.uiState.value.cartItems
        assertEquals(1, items.size)
        assertEquals("m2", items.first().menuItemId)
    }

    @Test
    fun `getEstimatedDeliveryTime defaults to 20 mins without an address`() = runTest {
        every { authRepository.getUserId() } returns flowOf("u1")
        val viewModel = build()

        assertEquals("20 mins", viewModel.getEstimatedDeliveryTime())
    }
}
