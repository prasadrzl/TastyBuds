package com.app.tastybuds.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class PricingModelTest {

    private fun cartItem(
        basePrice: Double,
        sizePrice: Double? = null,
        toppingPrices: List<Double> = emptyList(),
        quantity: Int = 1
    ) = CartItem(
        menuItemId = "m", name = "m", image = null, basePrice = basePrice,
        selectedSize = sizePrice?.let { OrderItemSize(id = "s", name = "s", additionalPrice = it) },
        selectedToppings = toppingPrices.map { OrderItemTopping(id = "t", name = "t", price = it) },
        selectedSpiceLevel = null, quantity = quantity, notes = null, restaurantId = null
    )

    @Test
    fun `calculateItemTotal adds size and toppings then multiplies by quantity`() {
        // (8 base + 2 size + 1 + 1 toppings) * 3 = 36
        val total = cartItem(basePrice = 8.0, sizePrice = 2.0, toppingPrices = listOf(1.0, 1.0), quantity = 3)
            .calculateItemTotal()
        assertEquals(36.0, total, 0.001)
    }

    @Test
    fun `calculateItemTotal treats a missing size as zero`() {
        assertEquals(10.0, cartItem(basePrice = 10.0).calculateItemTotal(), 0.001)
    }

    private fun voucher(
        discountType: String,
        discountValue: Double,
        minimumOrder: Double? = null,
        maxDiscount: Double? = null
    ) = AvailableVoucher(
        id = "v", userId = null, title = "t", description = null,
        discountType = discountType, discountValue = discountValue,
        minimumOrder = minimumOrder, maxDiscount = maxDiscount, restaurantId = null,
        validFrom = null, validUntil = null, isUsed = false, termsConditions = null
    )

    @Test
    fun `percentage discount applies the given rate`() {
        assertEquals(10.0, voucher("percentage", 10.0).calculateDiscount(100.0), 0.001)
    }

    @Test
    fun `percentage discount is capped by maxDiscount`() {
        assertEquals(5.0, voucher("percentage", 50.0, maxDiscount = 5.0).calculateDiscount(100.0), 0.001)
    }

    @Test
    fun `fixed amount discount returns the flat value`() {
        assertEquals(7.0, voucher("fixed_amount", 7.0).calculateDiscount(100.0), 0.001)
    }

    @Test
    fun `discount is zero below the minimum order`() {
        assertEquals(0.0, voucher("fixed_amount", 7.0, minimumOrder = 50.0).calculateDiscount(20.0), 0.001)
    }

    @Test
    fun `unknown discount type yields zero`() {
        assertEquals(0.0, voucher("mystery", 99.0).calculateDiscount(100.0), 0.001)
    }
}
