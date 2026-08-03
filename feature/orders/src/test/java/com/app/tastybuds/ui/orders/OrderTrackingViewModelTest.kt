package com.app.tastybuds.ui.orders

import com.app.tastybuds.domain.OrderUseCase
import com.app.tastybuds.domain.model.DeliveryAddress
import com.app.tastybuds.domain.model.Order
import com.app.tastybuds.domain.model.OrderStatus
import com.app.tastybuds.util.Result
import com.app.tastybuds.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrderTrackingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var orderUseCase: OrderUseCase

    private lateinit var viewModel: OrderTrackingViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { orderUseCase.getRestaurantLocation(any()) } returns Pair(1.0, 2.0)
        viewModel = OrderTrackingViewModel(orderUseCase)
    }

    private fun order(status: OrderStatus) = Order(
        id = "o1", userId = "user123456", restaurantId = "r1", status = status,
        orderItems = emptyList(), subtotal = 0.0, deliveryFee = 0.0, promotionDiscount = 0.0,
        totalAmount = 0.0, paymentMethod = "card", paymentStatus = "paid",
        deliveryAddress = DeliveryAddress(
            addressLine = "123 Main", street = null, city = null, state = null,
            postalCode = null, latitude = null, longitude = null, deliveryInstructions = null
        ),
        deliveryLat = null, deliveryLng = null, estimatedDeliveryTime = 30,
        actualDeliveryTime = null, specialNotes = null, createdAt = "", updatedAt = null
    )

    @Test
    fun `loadOrderDetails builds tracking state and progress steps`() = runTest {
        coEvery { orderUseCase.getOrderById("o1") } returns Result.Success(order(OrderStatus.CONFIRMED))

        viewModel.loadOrderDetails("o1")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.order)
        assertEquals("Customer #user12", state.customerName)
        assertEquals("Unknown", state.distance)            // no coordinates available
        assertEquals("30 mins", state.estimatedDeliveryTime)
        // CONFIRMED is index 1: step 0 completed, step 1 active
        assertTrue(state.progressSteps[0].isCompleted)
        assertTrue(state.progressSteps[1].isActive)
    }

    @Test
    fun `loadOrderDetails surfaces an error`() = runTest {
        coEvery { orderUseCase.getOrderById("o1") } returns Result.Error("not found")

        viewModel.loadOrderDetails("o1")

        assertNotNull(viewModel.uiState.value.error)
    }
}
