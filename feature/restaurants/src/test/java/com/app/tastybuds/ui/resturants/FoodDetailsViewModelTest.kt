package com.app.tastybuds.ui.resturants

import com.app.tastybuds.domain.FavoritesUseCase
import com.app.tastybuds.domain.FoodDetailsUseCase
import com.app.tastybuds.domain.model.FoodCustomization
import com.app.tastybuds.domain.model.FoodDetails
import com.app.tastybuds.domain.model.SizeOption
import com.app.tastybuds.domain.model.SpiceLevel
import com.app.tastybuds.domain.model.ToppingOption
import com.app.tastybuds.domain.repository.AuthRepository
import com.app.tastybuds.util.Result
import com.app.tastybuds.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FoodDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var foodDetailsUseCase: FoodDetailsUseCase

    @MockK
    private lateinit var favoritesUseCase: FavoritesUseCase

    @MockK
    private lateinit var authRepository: AuthRepository

    private lateinit var viewModel: FoodDetailsViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { authRepository.getUserId() } returns flowOf("u1")
        viewModel = FoodDetailsViewModel(foodDetailsUseCase, favoritesUseCase, authRepository)
    }

    private val customization = FoodCustomization(
        sizes = listOf(SizeOption(id = "s1", additionalPrice = 2.0f, isDefault = true)),
        toppings = listOf(ToppingOption(id = "t1", price = 1.0f, isDefault = true)),
        spiceLevels = listOf(SpiceLevel(id = "sp1", isDefault = true))
    )

    private fun stubSuccess(basePrice: Float = 10.0f) {
        coEvery { foodDetailsUseCase.getFoodDetails("f1", "u1") } returns
                Result.Success(FoodDetails(id = "f1", basePrice = basePrice))
        coEvery { foodDetailsUseCase.getCustomizationOptions("f1") } returns Result.Success(customization)
    }

    @Test
    fun `loadFoodDetails rejects a blank id`() = runTest {
        viewModel.loadFoodDetails("")

        assertEquals("Invalid food item ID", viewModel.uiState.value.error)
    }

    @Test
    fun `loadFoodDetails applies default selections and computes the total price`() = runTest {
        stubSuccess()

        viewModel.loadFoodDetails("f1")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.foodDetailsData)
        assertEquals("s1", state.selectedSize)
        assertEquals(listOf("t1"), state.selectedToppings)
        assertEquals("sp1", state.selectedSpiceLevel)
        // base 10 + size 2 + default topping 1 = 13
        assertEquals(13.0f, state.totalPrice)
        assertNull(state.error)
    }

    @Test
    fun `loadFoodDetails surfaces a use-case error`() = runTest {
        coEvery { foodDetailsUseCase.getFoodDetails("f1", "u1") } returns Result.Error("boom")
        coEvery { foodDetailsUseCase.getCustomizationOptions("f1") } returns Result.Error("boom")

        viewModel.loadFoodDetails("f1")

        assertNotNull(viewModel.uiState.value.error)
    }

    @Test
    fun `updateQuantity recomputes the total price`() = runTest {
        stubSuccess()
        viewModel.loadFoodDetails("f1")

        viewModel.updateQuantity(3)

        assertEquals(3, viewModel.uiState.value.quantity)
        assertEquals(39.0f, viewModel.uiState.value.totalPrice)  // 13 * 3
    }

    @Test
    fun `updateQuantity ignores non-positive values`() = runTest {
        stubSuccess()
        viewModel.loadFoodDetails("f1")

        viewModel.updateQuantity(0)

        assertEquals(1, viewModel.uiState.value.quantity)
    }

    @Test
    fun `toggleFavorite reflects the new favorite state`() = runTest {
        stubSuccess()
        viewModel.loadFoodDetails("f1")
        coEvery { favoritesUseCase.toggleMenuItemFavorite("u1", "f1") } returns Result.Success(true)

        viewModel.toggleFavorite()

        assertTrue(viewModel.uiState.value.foodDetailsData!!.foodDetails.isFavorite)
    }
}
