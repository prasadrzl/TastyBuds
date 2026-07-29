package com.app.tastybuds.data.model.mapper

import com.app.tastybuds.data.model.CreateOrderRequest
import com.app.tastybuds.data.model.DeliveryAddress
import com.app.tastybuds.data.model.OrderItemCustomization
import com.app.tastybuds.data.model.OrderItemRequest
import com.app.tastybuds.data.model.OrderItemSize as OrderItemSizeDto
import com.app.tastybuds.data.model.OrderItemSpiceLevel as OrderItemSpiceLevelDto
import com.app.tastybuds.data.model.OrderItemTopping as OrderItemToppingDto
import com.app.tastybuds.domain.model.CartItem
import com.app.tastybuds.domain.model.OrderItemSize
import com.app.tastybuds.domain.model.OrderItemSpiceLevel
import com.app.tastybuds.domain.model.OrderItemTopping
import com.app.tastybuds.domain.model.PlaceOrderData
import com.app.tastybuds.domain.model.UserAddress

// Maps the domain order command → wire DTO. Lives in the data layer (data.model.mapper).
fun PlaceOrderData.toCreateOrderRequest(): CreateOrderRequest = CreateOrderRequest(
    userId = userId,
    restaurantId = restaurantId,
    orderItems = items.map { it.toOrderItemRequest() },
    subtotal = subtotal,
    deliveryFee = deliveryFee,
    promotionDiscount = promotionDiscount,
    totalAmount = totalAmount,
    paymentMethod = paymentMethod,
    deliveryAddress = deliveryAddress.toDeliveryAddressDto(),
    deliveryLat = deliveryAddress.latitude,
    deliveryLng = deliveryAddress.longitude,
    estimatedDeliveryTime = estimatedDeliveryTime,
    specialNotes = specialNotes
)

fun CartItem.toOrderItemRequest(): OrderItemRequest = OrderItemRequest(
    menuItemId = menuItemId,
    name = name,
    image = image,
    basePrice = basePrice,
    quantity = quantity,
    customizations = OrderItemCustomization(
        size = selectedSize?.toDto(),
        toppings = selectedToppings.map { it.toDto() },
        spiceLevel = selectedSpiceLevel?.toDto()
    ),
    itemTotal = calculateItemTotal(),
    notes = notes
)

private fun OrderItemSize.toDto() = OrderItemSizeDto(id, name, additionalPrice)
private fun OrderItemTopping.toDto() = OrderItemToppingDto(id, name, price)
private fun OrderItemSpiceLevel.toDto() = OrderItemSpiceLevelDto(id, name, level)

fun UserAddress.toDeliveryAddressDto(): DeliveryAddress = DeliveryAddress(
    addressLine = addressLine,
    street = street,
    city = city,
    state = state,
    postalCode = postalCode,
    country = country ?: "Singapore",
    latitude = latitude,
    longitude = longitude,
    deliveryInstructions = deliveryInstructions
)
