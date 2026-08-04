package com.app.tastybuds.data.model.mapper

import com.app.tastybuds.data.model.ChatMessageDto
import com.app.tastybuds.domain.model.ChatMessage
import com.app.tastybuds.domain.model.SenderType
import org.junit.Assert.assertEquals
import org.junit.Test

class ChatMapperTest {

    @Test
    fun `toDomain maps every field and parses the sender type`() {
        val domain = ChatMessageDto(
            id = "m1", orderId = "o1", senderId = "d9",
            senderType = "DRIVER", content = "On my way", timestamp = 42L
        ).toDomain()

        assertEquals("m1", domain.id)
        assertEquals("o1", domain.orderId)
        assertEquals("d9", domain.senderId)
        assertEquals(SenderType.DRIVER, domain.senderType)
        assertEquals("On my way", domain.content)
        assertEquals(42L, domain.timestamp)
    }

    @Test
    fun `toDomain falls back to SYSTEM for an unknown sender type`() {
        val domain = ChatMessageDto(senderType = "robot").toDomain()
        assertEquals(SenderType.SYSTEM, domain.senderType)
    }

    @Test
    fun `toDto serializes the sender type back to its name`() {
        val dto = ChatMessage(
            id = "m2", orderId = "o1", senderId = "c1",
            senderType = SenderType.CUSTOMER, content = "hi", timestamp = 7L
        ).toDto()

        assertEquals("CUSTOMER", dto.senderType)
        assertEquals("hi", dto.content)
        assertEquals(7L, dto.timestamp)
    }
}
