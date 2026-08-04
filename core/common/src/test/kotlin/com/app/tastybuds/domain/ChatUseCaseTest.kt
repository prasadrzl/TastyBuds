package com.app.tastybuds.domain

import com.app.tastybuds.domain.repository.ChatRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ChatUseCaseTest {

    @MockK
    private lateinit var repository: ChatRepository

    private lateinit var useCase: ChatUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        useCase = ChatUseCase(repository)
    }

    @Test
    fun `connect delegates straight to the repository`() {
        val flow = emptyFlow<com.app.tastybuds.domain.model.ChatEvent>()
        every { repository.connect("o1", "u1") } returns flow

        assertSame(flow, useCase.connect("o1", "u1"))
        verify { repository.connect("o1", "u1") }
    }

    @Test
    fun `sendMessage refuses blank content and never touches the socket`() {
        assertFalse(useCase.sendMessage("   "))
        verify(exactly = 0) { repository.send(any()) }
    }

    @Test
    fun `sendMessage trims content before sending`() {
        every { repository.send("hello") } returns true

        assertTrue(useCase.sendMessage("  hello  "))
        verify { repository.send("hello") }
    }

    @Test
    fun `sendMessage propagates a false result from the repository`() {
        every { repository.send(any()) } returns false
        assertFalse(useCase.sendMessage("hi"))
    }

    @Test
    fun `disconnect delegates to the repository`() {
        useCase.disconnect()
        verify { repository.disconnect() }
    }
}
