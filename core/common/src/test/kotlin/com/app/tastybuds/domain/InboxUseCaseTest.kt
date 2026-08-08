package com.app.tastybuds.domain

import com.app.tastybuds.domain.model.NotificationItem
import com.app.tastybuds.domain.repository.InboxRepository
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class InboxUseCaseTest {

    @MockK
    private lateinit var repository: InboxRepository
    private lateinit var useCase: InboxUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        useCase = InboxUseCase(repository)
    }

    private fun item() = NotificationItem("n1", "t", "b", "ORDER", 1L, read = false)

    @Test
    fun `notifications and unreadCount delegate to the repository`() {
        val flow = emptyFlow<List<NotificationItem>>()
        every { repository.observeNotifications() } returns flow
        every { repository.unreadCount() } returns flowOf(3)

        assertSame(flow, useCase.notifications())
        useCase.unreadCount()

        verify { repository.observeNotifications() }
        verify { repository.unreadCount() }
    }

    @Test
    fun `save markRead markAllRead and clear delegate to the repository`() = runTest {
        useCase.save(item())
        useCase.markRead("n1")
        useCase.markAllRead()
        useCase.clear()

        coVerify { repository.saveIncoming(item()) }
        coVerify { repository.markAsRead("n1") }
        coVerify { repository.markAllAsRead() }
        coVerify { repository.clearAll() }
    }
}
