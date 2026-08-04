package com.app.tastybuds.ui.chat

import com.app.tastybuds.domain.ChatUseCase
import com.app.tastybuds.domain.model.ChatConnectionState
import com.app.tastybuds.domain.model.ChatEvent
import com.app.tastybuds.domain.model.ChatMessage
import com.app.tastybuds.domain.model.SenderType
import com.app.tastybuds.domain.repository.AuthRepository
import com.app.tastybuds.utils.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var chatUseCase: ChatUseCase

    @MockK
    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    private fun build() = ChatViewModel(chatUseCase, authRepository)

    private fun driverMessage() = ChatMessage(
        id = "m1", orderId = "o1", senderId = "driver_1",
        senderType = SenderType.DRIVER, content = "Two minutes away", timestamp = 1L
    )

    @Test
    fun `connect loads the user id and streams connection state plus messages`() {
        every { authRepository.getUserId() } returns flowOf("cust1")
        every { chatUseCase.connect("o1", "cust1") } returns flowOf(
            ChatEvent.ConnectionChanged(ChatConnectionState.CONNECTED),
            ChatEvent.MessageReceived(driverMessage())
        )

        val viewModel = build()
        viewModel.connect("o1")

        val state = viewModel.uiState.value
        assertEquals("cust1", state.currentUserId)
        assertEquals(ChatConnectionState.CONNECTED, state.connectionState)
        assertEquals(listOf("Two minutes away"), state.messages.map { it.content })
    }

    @Test
    fun `connect is idempotent and only subscribes once`()  {
        every { authRepository.getUserId() } returns flowOf("cust1")
        every { chatUseCase.connect(any(), any()) } returns flowOf()

        val viewModel = build()
        viewModel.connect("o1")
        viewModel.connect("o1")

        verify(exactly = 1) { chatUseCase.connect("o1", "cust1") }
    }

    @Test
    fun `onInputChange updates the input`() {
        val viewModel = build()
        viewModel.onInputChange("hey")
        assertEquals("hey", viewModel.uiState.value.input)
    }

    @Test
    fun `sendMessage optimistically appends the outgoing message and clears input`() {
        every { chatUseCase.sendMessage("hi driver") } returns true

        val viewModel = build()
        viewModel.onInputChange("hi driver")
        viewModel.sendMessage()

        val state = viewModel.uiState.value
        assertEquals("", state.input)
        assertEquals("hi driver", state.messages.last().content)
        assertEquals(SenderType.CUSTOMER, state.messages.last().senderType)
    }

    @Test
    fun `sendMessage does nothing for blank input`() {
        val viewModel = build()
        viewModel.sendMessage()

        assertTrue(viewModel.uiState.value.messages.isEmpty())
        verify(exactly = 0) { chatUseCase.sendMessage(any()) }
    }
}
