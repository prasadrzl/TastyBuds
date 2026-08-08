package com.app.tastybuds.ui.inbox

import app.cash.turbine.test
import com.app.tastybuds.domain.InboxUseCase
import com.app.tastybuds.domain.model.NotificationItem
import com.app.tastybuds.utils.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InboxViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var inboxUseCase: InboxUseCase

    @Before
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    private fun item(id: String, title: String) =
        NotificationItem(id, title, "body", "ORDER", 1L, read = false)

    @Test
    fun `notifications reflects the use case stream`() = runTest {
        every { inboxUseCase.notifications() } returns flowOf(listOf(item("n1", "Promo")))

        val viewModel = InboxViewModel(inboxUseCase)

        viewModel.notifications.test {
            // stateIn seeds with an empty list, then emits the mapped stream
            val emission = awaitItem().ifEmpty { awaitItem() }
            assertEquals(listOf("Promo"), emission.map { it.title })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `markRead delegates to the use case`() = runTest {
        every { inboxUseCase.notifications() } returns flowOf(emptyList())
        val viewModel = InboxViewModel(inboxUseCase)

        viewModel.markRead("n1")

        coVerify { inboxUseCase.markRead("n1") }
    }

    @Test
    fun `markAllRead delegates to the use case`() = runTest {
        every { inboxUseCase.notifications() } returns flowOf(emptyList())
        val viewModel = InboxViewModel(inboxUseCase)

        viewModel.markAllRead()

        coVerify { inboxUseCase.markAllRead() }
    }
}
