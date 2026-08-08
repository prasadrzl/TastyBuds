package com.app.tastybuds.data.model.mapper

import com.app.tastybuds.data.local.NotificationEntity
import com.app.tastybuds.domain.model.NotificationItem
import org.junit.Assert.assertEquals
import org.junit.Test

class NotificationMapperTest {

    @Test
    fun `entity maps to domain preserving every field`() {
        val d = NotificationEntity("n1", "Title", "Body", "ORDER", 42L, read = true, deepLink = "app://x").toDomain()
        assertEquals("n1", d.id)
        assertEquals("Title", d.title)
        assertEquals("Body", d.body)
        assertEquals("ORDER", d.type)
        assertEquals(42L, d.timestamp)
        assertEquals(true, d.read)
        assertEquals("app://x", d.deepLink)
    }

    @Test
    fun `domain maps back to entity`() {
        val e = NotificationItem("n2", "T", "B", "PROMO", 7L, read = false, deepLink = null).toEntity()
        assertEquals("n2", e.id)
        assertEquals("PROMO", e.type)
        assertEquals(false, e.read)
        assertEquals(null, e.deepLink)
    }
}
