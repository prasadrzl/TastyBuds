package com.app.tastybuds.domain

import com.app.tastybuds.domain.model.AvailableVoucher
import com.app.tastybuds.domain.model.CartItem
import com.app.tastybuds.domain.model.DeliveryAddress
import com.app.tastybuds.domain.model.Order
import com.app.tastybuds.domain.model.OrderItemSize
import com.app.tastybuds.domain.model.OrderItemTopping
import com.app.tastybuds.domain.model.OrderStatus
import com.app.tastybuds.domain.model.PlaceOrderData
import com.app.tastybuds.domain.model.UserAddress
import com.app.tastybuds.domain.repository.OrderRepository
import com.app.tastybuds.util.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrderUseCaseTest {

    @MockK
    private lateinit var repository: OrderRepository

    private lateinit var useCase: OrderUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = OrderUseCase(repository)
    }

    // basePrice 10 + size 1.0 + topping 0.5 = 11.5, x2 = 23.0
    private fun cartItem() = CartItem(
        menuItemId = "m1",
        name = "Fried Chicken",
        image = null,
        basePrice = 10.0,
        selectedSize = OrderItemSize("s", "L", 1.0),
        selectedToppings = listOf(OrderItemTopping("t", "Cheese", 0.5)),
        selectedSpiceLevel = null,
        quantity = 2,
        notes = null,
        restaurantId = "rest_001"
    )

    private fun address(city: String? = "tampines") = UserAddress(
        id = "a1", userId = "u1", addressLine = "1 Test St", street = null, city = city,
        state = null, postalCode = null, country = null, latitude = null, longitude = null,
        addressType = null, addressLabel = null, isDefault = true, deliveryInstructions = null,
        createdAt = null
    )

    private fun voucher(discountType: String = "percentage", value: Double = 10.0, minimumOrder: Double? = null) =
        AvailableVoucher(
            id = "v1", userId = null, title = "10% off", description = null,
            discountType = discountType, discountValue = value, minimumOrder = minimumOrder,
            maxDiscount = null, restaurantId = null, validFrom = null, validUntil = null,
            isUsed = false, termsConditions = null
        )

    // ---------- createOrder ----------

    @Test
    fun `createOrder returns Error when the cart is empty`() = runTest {
        val result = useCase.createOrder("u1", emptyList(), address())

        assertTrue(result is Result.Error)
        coVerify(exactly = 0) { repository.createOrder(any()) }
    }

    @Test
    fun `createOrder computes subtotal, delivery fee and total with no voucher`() = runTest {
        val slot = slot<PlaceOrderData>()
        coEvery { repository.createOrder(capture(slot)) } returns Result.Success(mockk(relaxed = true))

        useCase.createOrder("u1", listOf(cartItem()), address())

        val order = slot.captured
        assertEquals(23.0, order.subtotal, 0.001)   // (10 + 1 + 0.5) * 2
        assertEquals(2.0, order.deliveryFee, 0.001)
        assertEquals(0.0, order.promotionDiscount, 0.001)
        assertEquals(25.0, order.totalAmount, 0.001) // 23 + 2 - 0
        assertEquals("rest_001", order.restaurantId)
    }

    @Test
    fun `createOrder applies a percentage voucher discount to the total`() = runTest {
        val slot = slot<PlaceOrderData>()
        coEvery { repository.createOrder(capture(slot)) } returns Result.Success(mockk(relaxed = true))

        useCase.createOrder("u1", listOf(cartItem()), address(), selectedVoucher = voucher(value = 10.0))

        val order = slot.captured
        assertEquals(2.3, order.promotionDiscount, 0.001)  // 10% of 23
        assertEquals(22.7, order.totalAmount, 0.001)       // 23 + 2 - 2.3
    }

    @Test
    fun `createOrder fails when the subtotal is below the voucher minimum order`() = runTest {
        val result = useCase.createOrder(
            "u1", listOf(cartItem()), address(), selectedVoucher = voucher(minimumOrder = 100.0)
        )

        assertTrue(result is Result.Error)
        coVerify(exactly = 0) { repository.createOrder(any()) }
    }

    @Test
    fun `createOrder sets the estimated delivery time from the address city`() = runTest {
        val slot = slot<PlaceOrderData>()
        coEvery { repository.createOrder(capture(slot)) } returns Result.Success(mockk(relaxed = true))

        useCase.createOrder("u1", listOf(cartItem()), address(city = "tampines"))

        assertEquals(25, slot.captured.estimatedDeliveryTime) // tampines bucket
    }

    // ---------- pure calculations ----------

    @Test
    fun `calculateDeliveryTimeFromDistance maps distance bands to minutes`() {
        assertEquals(15, useCase.calculateDeliveryTimeFromDistance(2.0))
        assertEquals(20, useCase.calculateDeliveryTimeFromDistance(5.0))
        assertEquals(30, useCase.calculateDeliveryTimeFromDistance(10.0))
        assertEquals(40, useCase.calculateDeliveryTimeFromDistance(15.0))
        assertEquals(50, useCase.calculateDeliveryTimeFromDistance(25.0))
    }

    @Test
    fun `getRestaurantLocation returns known coordinates and a default for unknown ids`() {
        assertEquals(Pair(1.4043, 103.9010), useCase.getRestaurantLocation("rest_002"))
        assertEquals(Pair(1.3966, 103.9072), useCase.getRestaurantLocation("unknown"))
    }

    // ---------- delegation ----------

    @Test
    fun `getAvailableVouchers delegates to the repository`() = runTest {
        val vouchers = listOf(voucher())
        coEvery { repository.getAvailableVouchers("u1") } returns Result.Success(vouchers)

        val result = useCase.getAvailableVouchers("u1")

        assertEquals(vouchers, (result as Result.Success).data)
        coVerify { repository.getAvailableVouchers("u1") }
    }

    @Test
    fun `getUserAddresses delegates to the repository`() = runTest {
        coEvery { repository.getUserAddresses("u1") } returns Result.Success(listOf(address()))

        assertTrue(useCase.getUserAddresses("u1") is Result.Success)
        coVerify { repository.getUserAddresses("u1") }
    }

    @Test
    fun `createOrder maps a repository exception to an error`() = runTest {
        coEvery { repository.createOrder(any()) } throws RuntimeException("db down")

        val result = useCase.createOrder("u1", listOf(cartItem()), address())

        assertTrue(result is Result.Error)
    }

    @Test
    fun `createOrder derives estimated delivery time per city bucket`() = runTest {
        val slot = slot<PlaceOrderData>()
        coEvery { repository.createOrder(capture(slot)) } returns Result.Success(mockk(relaxed = true))

        useCase.createOrder("u1", listOf(cartItem()), address(city = "marina bay"))
        assertEquals(15, slot.captured.estimatedDeliveryTime)

        useCase.createOrder("u1", listOf(cartItem()), address(city = "sengkang"))
        assertEquals(20, slot.captured.estimatedDeliveryTime)

        useCase.createOrder("u1", listOf(cartItem()), address(city = "jurong west"))
        assertEquals(30, slot.captured.estimatedDeliveryTime)

        useCase.createOrder("u1", listOf(cartItem()), address(city = "woodlands"))
        assertEquals(35, slot.captured.estimatedDeliveryTime)

        useCase.createOrder("u1", listOf(cartItem()), address(city = "nowhere"))
        assertEquals(20, slot.captured.estimatedDeliveryTime)
    }

    @Test
    fun `getRestaurantLocation covers all known ids`() {
        assertEquals(Pair(1.3966, 103.9072), useCase.getRestaurantLocation("rest_001"))
        assertEquals(Pair(1.3914, 103.8946), useCase.getRestaurantLocation("rest_003"))
        assertEquals(Pair(1.3966, 103.9072), useCase.getRestaurantLocation(null))
    }

    // ---------- order fetch + normalization ----------

    private fun order(restaurantName: String? = null) = Order(
        id = "o1", userId = "u1", restaurantId = "rest_001", status = OrderStatus.PENDING,
        orderItems = emptyList(), subtotal = 0.0, deliveryFee = 0.0, promotionDiscount = 0.0,
        totalAmount = 0.0, paymentMethod = "e-wallet", paymentStatus = "paid",
        deliveryAddress = DeliveryAddress(
            addressLine = "1 Test St", street = null, city = null, state = null,
            postalCode = null, latitude = null, longitude = null, deliveryInstructions = null
        ),
        deliveryLat = null, deliveryLng = null, estimatedDeliveryTime = 20,
        actualDeliveryTime = null, specialNotes = null, createdAt = "", updatedAt = null,
        restaurantName = restaurantName
    )

    @Test
    fun `getUserOrders defaults a missing restaurant name to Unknown Restaurant`() = runTest {
        coEvery { repository.getUserOrders("u1") } returns Result.Success(listOf(order(restaurantName = null)))

        val result = useCase.getUserOrders("u1")

        assertEquals("Unknown Restaurant", (result as Result.Success).data.first().restaurantName)
    }

    @Test
    fun `getUserOrders propagates a repository error`() = runTest {
        coEvery { repository.getUserOrders("u1") } returns Result.Error("boom")
        assertTrue(useCase.getUserOrders("u1") is Result.Error)
    }

    @Test
    fun `getOrderById keeps an existing restaurant name`() = runTest {
        coEvery { repository.getOrderById("o1") } returns Result.Success(order(restaurantName = "Pizza Place"))

        val result = useCase.getOrderById("o1")

        assertEquals("Pizza Place", (result as Result.Success).data.restaurantName)
    }

    @Test
    fun `getOrderById propagates a repository error`() = runTest {
        coEvery { repository.getOrderById("o1") } returns Result.Error("not found")
        assertTrue(useCase.getOrderById("o1") is Result.Error)
    }
}
