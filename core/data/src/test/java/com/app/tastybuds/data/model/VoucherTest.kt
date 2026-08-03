package com.app.tastybuds.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class VoucherTest {

    private fun voucher(
        discountType: String,
        discountValue: Double,
        minimumOrder: Double? = null,
        maxDiscount: Double? = null
    ) = Voucher(
        discountType = discountType, discountValue = discountValue,
        minimumOrder = minimumOrder, maxDiscount = maxDiscount
    )

    @Test
    fun `percentage discount applies the rate`() {
        assertEquals(15.0, voucher("percentage", 15.0).calculateDiscount(100.0), 0.001)
    }

    @Test
    fun `percentage discount respects maxDiscount cap`() {
        assertEquals(8.0, voucher("percentage", 40.0, maxDiscount = 8.0).calculateDiscount(100.0), 0.001)
    }

    @Test
    fun `fixed amount returns the flat value`() {
        assertEquals(5.0, voucher("fixed_amount", 5.0).calculateDiscount(100.0), 0.001)
    }

    @Test
    fun `below minimum order gives no discount`() {
        assertEquals(0.0, voucher("percentage", 20.0, minimumOrder = 50.0).calculateDiscount(30.0), 0.001)
    }

    @Test
    fun `unknown type gives no discount`() {
        assertEquals(0.0, voucher("bogus", 20.0).calculateDiscount(100.0), 0.001)
    }
}
