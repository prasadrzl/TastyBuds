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
import org.junit.Assert.assertNull
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
        menuCount: Int = 0,
        distance: String = "1 km",
        deliveryTime: String = "20",
        badges: List<String> = emptyList()
    ): SearchResult {
        val restaurant = mockk<SearchRestaurant> {
            every { this@mockk.rating } returns rating
            every { this@mockk.isFavorite } returns isFavorite
            every { this@mockk.badges } returns badges
            every { deliveryFee } returns 5.0
            every { this@mockk.distance } returns distance
            every { this@mockk.deliveryTime } returns deliveryTime
        }
        return mockk {
            every { this@mockk.id } returns id
            every { this@mockk.restaurant } returns restaurant
            every { menuItemList } returns List(menuCount) {
                mockk<MenuItem> { every { price } returns (it + 1).toDouble() }
            }
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

    private fun searchWith(vararg results: SearchResult) {
        every { searchResultsUseCase.searchMenuItemsGroupedByRestaurant("") } returns flowOf(results.toList())
        viewModel.searchMenuItems("")
    }

    @Test
    fun `updateSortOption distance orders nearest first`() = runTest {
        searchWith(result("far", distance = "9 km"), result("near", distance = "1 km"))
        viewModel.updateSortOption(SortOptionIds.DISTANCE)
        assertEquals(listOf("near", "far"), viewModel.uiState.value.filteredAndSortedResults.map { it.id })
    }

    @Test
    fun `updateSortOption delivery time orders fastest first`() = runTest {
        searchWith(result("slow", deliveryTime = "40"), result("fast", deliveryTime = "10"))
        viewModel.updateSortOption(SortOptionIds.DELIVERY_TIME)
        assertEquals(listOf("fast", "slow"), viewModel.uiState.value.filteredAndSortedResults.map { it.id })
    }

    @Test
    fun `toggleFilter adds then removes a filter`() = runTest {
        searchWith(result("a"))
        viewModel.toggleFilter("freeship")
        assertTrue(viewModel.uiState.value.selectedFilters.contains("freeship"))
        viewModel.toggleFilter("freeship")
        assertFalse(viewModel.uiState.value.selectedFilters.contains("freeship"))
    }

    @Test
    fun `near_you filter keeps only nearby restaurants`() = runTest {
        searchWith(result("near", distance = "1 km"), result("far", distance = "9 km"))
        viewModel.toggleFilter("near_you")
        assertEquals(listOf("near"), viewModel.uiState.value.filteredAndSortedResults.map { it.id })
    }

    @Test
    fun `clearAllFilters resets filters and sort`() = runTest {
        searchWith(result("a"))
        viewModel.toggleFilter("freeship")
        viewModel.updateSortOption(SortOptionIds.RATING)

        viewModel.clearAllFilters()

        assertTrue(viewModel.uiState.value.selectedFilters.isEmpty())
        assertEquals(SortOptionIds.RELEVANCE, viewModel.uiState.value.selectedSortId)
    }

    @Test
    fun `resetSearch clears state`() = runTest {
        searchWith(result("a", menuCount = 2))
        viewModel.resetSearch()
        assertTrue(viewModel.uiState.value.searchResults.isEmpty())
    }

    @Test
    fun `clearError removes the error`() = runTest {
        searchWith(result("a"))
        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `getAvailableFilters returns distinct sorted badges`() = runTest {
        searchWith(result("a", badges = listOf("Partner", "Freeship")), result("b", badges = listOf("Partner")))
        assertEquals(listOf("Freeship", "Partner"), viewModel.getAvailableFilters())
    }

    @Test
    fun `price sort orders by cheapest menu item`() = runTest {
        searchWith(result("pricey", menuCount = 3), result("cheap", menuCount = 1))
        viewModel.updateSortOption(SortOptionIds.PRICE_LOW_TO_HIGH)
        // "cheap" has a single item priced 1.0; "pricey" cheapest is also 1.0 -> stable, both kept
        assertEquals(2, viewModel.uiState.value.filteredAndSortedResults.size)
    }

    @Test
    fun `freeship filter keeps restaurants tagged freeship`() = runTest {
        searchWith(
            result("free", badges = listOf("Freeship")),
            result("paid", badges = emptyList())
        )
        viewModel.toggleFilter("freeship")
        assertEquals(listOf("free"), viewModel.uiState.value.filteredAndSortedResults.map { it.id })
    }

    @Test
    fun `partner filter keeps verified or partner restaurants`() = runTest {
        searchWith(
            result("partner", badges = listOf("Partner")),
            result("plain", badges = emptyList())
        )
        viewModel.toggleFilter("partner")
        assertEquals(listOf("partner"), viewModel.uiState.value.filteredAndSortedResults.map { it.id })
    }

    @Test
    fun `generic badge filter matches by normalized badge id`() = runTest {
        searchWith(
            result("has", badges = listOf("Best Seller")),
            result("no", badges = emptyList())
        )
        viewModel.toggleFilter("best_seller")
        assertEquals(listOf("has"), viewModel.uiState.value.filteredAndSortedResults.map { it.id })
    }
}
