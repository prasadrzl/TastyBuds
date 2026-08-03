package com.app.tastybuds.ui.resturants.search

import com.app.tastybuds.domain.SearchResultsUseCase
import com.app.tastybuds.domain.model.MenuItem
import com.app.tastybuds.domain.model.SearchResult
import com.app.tastybuds.domain.model.SearchRestaurant
import com.app.tastybuds.utils.MainDispatcherRule
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
class SearchResultsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var searchResultsUseCase: SearchResultsUseCase

    private lateinit var viewModel: SearchResultsViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = SearchResultsViewModel(searchResultsUseCase)
    }

    private fun result(
        id: String,
        rating: Float = 0f,
        isFavorite: Boolean = false,
        menuCount: Int = 0
    ): SearchResult {
        val restaurant = mockk<SearchRestaurant> {
            every { this@mockk.rating } returns rating
            every { this@mockk.isFavorite } returns isFavorite
            every { badges } returns emptyList()
            every { deliveryFee } returns 5.0
            every { distance } returns "1 km"
            every { deliveryTime } returns "20"
        }
        return mockk {
            every { this@mockk.id } returns id
            every { this@mockk.restaurant } returns restaurant
            every { menuItemList } returns List(menuCount) { mockk<MenuItem>() }
        }
    }

    @Test
    fun `searchMenuItems marks the state empty for no results`() = runTest {
        every { searchResultsUseCase.searchMenuItemsGroupedByRestaurant("x") } returns flowOf(emptyList())

        viewModel.searchMenuItems("x")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isEmpty)
    }

    @Test
    fun `searchMenuItems populates results`() = runTest {
        every { searchResultsUseCase.searchMenuItemsGroupedByRestaurant("pizza") } returns
                flowOf(listOf(result("a"), result("b")))

        viewModel.searchMenuItems("pizza")

        val state = viewModel.uiState.value
        assertFalse(state.isEmpty)
        assertEquals(2, state.searchResults.size)
        assertEquals(2, state.filteredAndSortedResults.size)
    }

    @Test
    fun `toggleFilter favorite keeps only favorite restaurants`() = runTest {
        every { searchResultsUseCase.searchMenuItemsGroupedByRestaurant("") } returns
                flowOf(listOf(result("fav", isFavorite = true), result("plain", isFavorite = false)))
        viewModel.searchMenuItems("")

        viewModel.toggleFilter("favorite")

        assertEquals(listOf("fav"), viewModel.uiState.value.filteredAndSortedResults.map { it.id })
    }

    @Test
    fun `updateSortOption rating sorts by rating descending`() = runTest {
        every { searchResultsUseCase.searchMenuItemsGroupedByRestaurant("") } returns
                flowOf(listOf(result("low", rating = 3f), result("high", rating = 5f)))
        viewModel.searchMenuItems("")

        viewModel.updateSortOption(SortOptionIds.RATING)

        assertEquals(listOf("high", "low"), viewModel.uiState.value.filteredAndSortedResults.map { it.id })
    }

    @Test
    fun `getMenuItemsCount sums menu items across results`() = runTest {
        every { searchResultsUseCase.searchMenuItemsGroupedByRestaurant("") } returns
                flowOf(listOf(result("a", menuCount = 2), result("b", menuCount = 3)))
        viewModel.searchMenuItems("")

        assertEquals(5, viewModel.getMenuItemsCount())
    }
}
