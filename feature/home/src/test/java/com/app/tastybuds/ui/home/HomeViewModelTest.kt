package com.app.tastybuds.ui.home

import com.app.tastybuds.domain.HomeData
import com.app.tastybuds.domain.HomeUseCase
import com.app.tastybuds.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
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
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var homeUseCase: HomeUseCase

    @Before
    fun setUp() = MockKAnnotations.init(this)

    private fun homeData(voucherCount: Int = 0) = HomeData(
        banners = emptyList(), categories = emptyList(), voucherCount = voucherCount,
        collections = emptyList(), recommendedRestaurants = emptyList(), deals = emptyList()
    )

    @Test
    fun `loads home data into the UI state on init`() = runTest {
        coEvery { homeUseCase.getHomeData() } returns homeData(voucherCount = 3)

        val viewModel = HomeViewModel(homeUseCase)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(3, state.voucherCount)
        assertNull(state.error)
    }

    @Test
    fun `exposes an error in the UI state when loading fails`() = runTest {
        coEvery { homeUseCase.getHomeData() } throws RuntimeException("boom")

        val viewModel = HomeViewModel(homeUseCase)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
    }
}
