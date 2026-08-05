package com.app.tastybuds.ui.vouchers

import com.app.tastybuds.domain.VouchersUseCase
import com.app.tastybuds.domain.model.Voucher
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VouchersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var vouchersUseCase: VouchersUseCase

    @MockK
    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `loads vouchers for the signed-in user`() = runTest {
        every { authRepository.getUserId() } returns flowOf("u1")
        coEvery { vouchersUseCase.getActiveVouchers("u1") } returns Result.Success(listOf(mockk<Voucher>()))
        coEvery { vouchersUseCase.getUsedVouchers("u1") } returns Result.Success(emptyList())

        val viewModel = VouchersViewModel(vouchersUseCase, authRepository)

        val state = viewModel.uiState.value
        assertEquals(1, state.activeVouchers.size)
        assertNull(state.error)
    }

    @Test
    fun `prompts to log in when there is no user id`() = runTest {
        every { authRepository.getUserId() } returns flowOf(null)

        val viewModel = VouchersViewModel(vouchersUseCase, authRepository)

        assertNotNull(viewModel.uiState.value.error)
    }

    private fun loadedViewModel(): VouchersViewModel {
        every { authRepository.getUserId() } returns flowOf("u1")
        coEvery { vouchersUseCase.getActiveVouchers("u1") } returns Result.Success(listOf(mockk<Voucher>()))
        coEvery { vouchersUseCase.getUsedVouchers("u1") } returns Result.Success(emptyList())
        return VouchersViewModel(vouchersUseCase, authRepository)
    }

    @Test
    fun `selectTab updates the selected tab`() = runTest {
        val viewModel = loadedViewModel()
        viewModel.selectTab(1)
        assertEquals(1, viewModel.uiState.value.selectedTab)
    }

    @Test
    fun `clearError resets the error`() = runTest {
        every { authRepository.getUserId() } returns flowOf(null)
        val viewModel = VouchersViewModel(vouchersUseCase, authRepository)

        viewModel.clearError()

        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `refreshVouchers reloads without error`() = runTest {
        val viewModel = loadedViewModel()
        coEvery { vouchersUseCase.refreshVouchers("u1") } returns Result.Success(emptyList())

        viewModel.refreshVouchers()

        assertNull(viewModel.uiState.value.error)
    }
}
