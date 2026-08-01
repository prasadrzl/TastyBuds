package com.app.tastybuds.domain

import app.cash.turbine.test
import com.app.tastybuds.domain.model.MenuItem
import com.app.tastybuds.domain.model.SearchResult
import com.app.tastybuds.domain.repository.SearchRepository
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchResultsUseCaseTest {

    @MockK
    private lateinit var repository: SearchRepository
    private lateinit var useCase: SearchResultsUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = SearchResultsUseCase(repository)
    }

    @Test
    fun `search filters out results whose menu item list is empty`() = runTest {
        coEvery { repository.searchMenuItemsWithRestaurants("pizza") } returns listOf(
            SearchResult(id = "keep", menuItemList = listOf(mockk<MenuItem>())),
            SearchResult(id = "drop", menuItemList = emptyList())
        )

        useCase.searchMenuItemsGroupedByRestaurant("pizza").test {
            assertEquals(listOf("keep"), awaitItem().map { it.id })
            awaitComplete()
        }
    }

    @Test
    fun `search emits an empty list when the repository throws`() = runTest {
        coEvery { repository.searchMenuItemsWithRestaurants(any()) } throws RuntimeException("boom")

        useCase.searchMenuItemsGroupedByRestaurant("x").test {
            assertEquals(emptyList<SearchResult>(), awaitItem())
            awaitComplete()
        }
    }
}
