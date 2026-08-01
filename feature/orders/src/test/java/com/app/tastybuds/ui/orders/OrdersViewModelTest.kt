package com.app.tastybuds.ui.orders

import com.app.tastybuds.domain.OrderUseCase
import com.app.tastybuds.domain.model.Order
import com.app.tastybuds.domain.model.User
import com.app.tastybuds.domain.repository.AuthRepository
import com.app.tastybuds.util.Result
import com.app.tastybuds.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrdersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var orderUseCase: OrderUseCase

    @MockK
    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() = MockKAnnotations.init(this)

    private fun order(createdAt: String): Order = mockk {
        every { this@mockk.createdAt } returns createdAt
    }

    @Test
    fun `loads and sorts the current user's orders by newest first`() = runTest {
        val user = mockk<User> { every { id } returns "u1" }
        coEvery { authRepository.getCurrentUser() } returns Result.Success(user)
        coEvery { orderUseCase.getUserOrders("u1") } returns Result.Success(
            listOf(order("2024-01-01"), order("2024-03-01"), order("2024-02-01"))
        )

        val viewModel = OrdersViewModel(orderUseCase, authRepository)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(listOf("2024-03-01", "2024-02-01", "2024-01-01"), state.orders.map { it.createdAt })
    }

    @Test
    fun `surfaces an error when the current user cannot be loaded`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Result.Error("no user")

        val viewModel = OrdersViewModel(orderUseCase, authRepository)

        assertNotNull(viewModel.uiState.value.error)
    }
}
