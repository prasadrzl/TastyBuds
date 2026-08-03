package com.app.tastybuds.feature.orders.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocationUtilsTest {

    @Test
    fun `distance between identical points is zero`() {
        assertEquals(0.0, LocationUtils.calculateDistance(1.3521, 103.8198, 1.3521, 103.8198), 0.0001)
    }

    @Test
    fun `distance is symmetric`() {
        val a = LocationUtils.calculateDistance(1.30, 103.80, 1.40, 103.90)
        val b = LocationUtils.calculateDistance(1.40, 103.90, 1.30, 103.80)
        assertEquals(a, b, 0.0001)
    }

    @Test
    fun `one degree of latitude is roughly 111 km`() {
        val km = LocationUtils.calculateDistance(0.0, 0.0, 1.0, 0.0)
        assertTrue("expected ~111km but was $km", km in 110.0..112.0)
    }
}
