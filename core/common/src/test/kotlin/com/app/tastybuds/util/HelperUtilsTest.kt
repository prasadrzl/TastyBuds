package com.app.tastybuds.util

import com.app.tastybuds.domain.model.FoodCustomization
import com.app.tastybuds.domain.model.FoodDetails
import com.app.tastybuds.domain.model.FoodDetailsData
import com.app.tastybuds.domain.model.SizeOption
import com.app.tastybuds.domain.model.ToppingOption
import org.junit.Assert.assertEquals
import org.junit.Test

class HelperUtilsTest {

    private val data = FoodDetailsData(
        foodDetails = FoodDetails(id = "f1", basePrice = 10.0f),
        customization = FoodCustomization(
            sizes = listOf(
                SizeOption(id = "small", additionalPrice = 0.0f),
                SizeOption(id = "large", additionalPrice = 2.0f)
            ),
            toppings = listOf(
                ToppingOption(id = "cheese", price = 1.0f),
                ToppingOption(id = "bacon", price = 3.0f)
            )
        )
    )

    @Test
    fun `calculateTotalPrice sums base, size and toppings times quantity`() {
        // (10 base + 2 large + 1 cheese + 3 bacon) * 2 = 32
        val price = calculateTotalPrice(data, "large", listOf("cheese", "bacon"), 2)
        assertEquals(32.0f, price, 0.001f)
    }

    @Test
    fun `calculateTotalPrice ignores unknown size and topping ids`() {
        // only base price counts: 10 * 1
        val price = calculateTotalPrice(data, "nope", listOf("ghost"), 1)
        assertEquals(10.0f, price, 0.001f)
    }

    @Test
    fun `formatOrderDate returns the original string when it cannot be parsed`() {
        assertEquals("not-a-date", formatOrderDate("not-a-date"))
    }

    @Test
    fun `formatOrderDate reformats a valid ISO timestamp`() {
        val formatted = formatOrderDate("2024-01-15T10:30:00.000000")
        // Reformatted output differs from input and keeps the year.
        assertEquals(true, formatted != "2024-01-15T10:30:00.000000")
        assertEquals(true, formatted.contains("2024"))
    }
}
