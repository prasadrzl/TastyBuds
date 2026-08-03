package com.app.tastybuds.feature.restaurants.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchUtilsTest {

    @Test fun `null or blank is not near`() {
        assertFalse(isRestaurantNear(null))
        assertFalse(isRestaurantNear(""))
    }

    @Test fun `meters within 500 is near`() {
        assertTrue(isRestaurantNear("300 m"))
    }

    @Test fun `meters beyond 500 is not near`() {
        assertFalse(isRestaurantNear("600 m"))
    }

    @Test fun `kilometers within 2 is near`() {
        assertTrue(isRestaurantNear("1.5 km"))
    }

    @Test fun `kilometers beyond 2 is not near`() {
        assertFalse(isRestaurantNear("3 km"))
    }

    @Test fun `unrecognized unit is not near`() {
        assertFalse(isRestaurantNear("far away"))
    }
}
