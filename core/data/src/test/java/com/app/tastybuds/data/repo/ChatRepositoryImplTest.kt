package com.app.tastybuds.data.repo

import com.app.tastybuds.domain.model.ChatConnectionState
import com.app.tastybuds.domain.model.ChatEvent
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ChatRepositoryImplTest {

    private lateinit var dataSource: ChatWebSocketDataSource
    private lateinit var repository: ChatRepositoryImpl

    @Before
    fun setUp() {
        dataSource = mockk(relaxed = true)
        val tokenStore = mockk<AuthTokenStore>(relaxed = true)
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        repository = ChatRepositoryImpl(dataSource, tokenStore, moshi)
    }

    @Test
    fun `connect prepends CONNECTING then maps frames to domain events`() = runTest {
        val json = """{"id":"m1","orderId":"o1","senderId":"driver_1",""" +
                """"senderType":"DRIVER","content":"hi","timestamp":5}"""
        every { dataSource.connect(any()) } returns flowOf(
            SocketFrame.Open,
            SocketFrame.Message(json),
            SocketFrame.Failure
        )

        val events = repository.connect("o1", "u1").toList()

        assertEquals(4, events.size)
        assertEquals(
            ChatConnectionState.CONNECTING,
            (events[0] as ChatEvent.ConnectionChanged).state
        )
        assertEquals(
            ChatConnectionState.CONNECTED,
            (events[1] as ChatEvent.ConnectionChanged).state
        )
        assertEquals("hi", (events[2] as ChatEvent.MessageReceived).message.content)
        assertEquals(
            ChatConnectionState.ERROR,
            (events[3] as ChatEvent.ConnectionChanged).state
        )
    }

    @Test
    fun `connect drops an unparseable inbound message`() = runTest {
        every { dataSource.connect(any()) } returns flowOf(
            SocketFrame.Open,
            SocketFrame.Message("not-json")
        )

        val events = repository.connect("o1", "u1").toList()

        // CONNECTING + CONNECTED only; the junk message produced no event.
        assertEquals(2, events.size)
        assertTrue(events.none { it is ChatEvent.MessageReceived })
    }

    @Test
    fun `send serializes an outgoing customer message to the data source`() = runTest {
        val captured = slot<String>()
        every { dataSource.send(capture(captured)) } returns true

        repository.connect("order_9", "user_7")  // sets orderId/userId synchronously
        val result = repository.send("see you soon")

        assertTrue(result)
        val payload = captured.captured
        assertTrue(payload.contains("\"senderType\":\"CUSTOMER\""))
        assertTrue(payload.contains("\"senderId\":\"user_7\""))
        assertTrue(payload.contains("\"orderId\":\"order_9\""))
        assertTrue(payload.contains("\"content\":\"see you soon\""))
    }

    @Test
    fun `disconnect delegates to the data source`() = runTest {
        repository.disconnect()
        io.mockk.verify { dataSource.disconnect() }
    }
}
