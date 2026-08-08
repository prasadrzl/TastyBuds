package com.app.tastybuds.data.repo

import com.app.tastybuds.data.local.NotificationDao
import com.app.tastybuds.data.local.NotificationEntity
import com.app.tastybuds.domain.model.NotificationItem
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class InboxRepositoryImplTest {

    private lateinit var dao: NotificationDao
    private lateinit var repository: InboxRepositoryImpl

    @Before
    fun setUp() {
        dao = mockk(relaxUnitFun = true)
        repository = InboxRepositoryImpl(dao)
    }

    @Test
    fun `observeNotifications maps entities to domain`() = runTest {
        every { dao.observeAll() } returns flowOf(
            listOf(NotificationEntity("n1", "T", "B", "ORDER", 1L, read = false, deepLink = null))
        )

        val result = repository.observeNotifications().first()

        assertEquals(listOf("n1"), result.map { it.id })
    }

    @Test
    fun `unreadCount delegates to the dao`() = runTest {
        every { dao.unreadCount() } returns flowOf(5)
        assertEquals(5, repository.unreadCount().first())
    }

    @Test
    fun `saveIncoming upserts the mapped entity`() = runTest {
        val captured = slot<NotificationEntity>()
        coVerify(exactly = 0) { dao.upsert(any()) }

        repository.saveIncoming(NotificationItem("n9", "T", "B", "PROMO", 3L, read = false))

        coVerify { dao.upsert(capture(captured)) }
        assertEquals("n9", captured.captured.id)
    }

    @Test
    fun `mark and clear operations delegate to the dao`() = runTest {
        repository.markAsRead("n1")
        repository.markAllAsRead()
        repository.clearAll()

        coVerify { dao.markRead("n1") }
        coVerify { dao.markAllRead() }
        coVerify { dao.clear() }
    }
}
