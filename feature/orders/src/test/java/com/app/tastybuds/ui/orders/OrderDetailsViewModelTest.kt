package com.app.tastybuds.ui.orders

import com.app.tastybuds.domain.OrderUseCase
import com.app.tastybuds.domain.model.Order
import com.app.tastybuds.util.Result
import com.app.tastybuds.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrderDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var orderUseCase: OrderUseCase

    @Before
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `loadOrderDetails puts the order into the UI state`() = runTest {
        val order = mockk<Order>(relaxed = true)
        coEvery { orderUseCase.getOrderById("o1") } returns Result.Success(order)

        val viewModel = OrderDetailsViewModel(orderUseCase)
        viewModel.loadOrderDetails("o1")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(order, state.order)
        assertNull(state.error)
    }

    @Test
    fun `loadOrderDetails surfaces a repository error`() = runTest {
        coEvery { orderUseCase.getOrderById("o1") } returns Result.Error("not found")

        val viewModel = OrderDetailsViewModel(orderUseCase)
        viewModel.loadOrderDetails("o1")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
    }
}
