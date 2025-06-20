package com.app.tastybuds.data.model

data class CartItem(
    val menuItemId: String,
    val name: String,
    val image: String?,
    val basePrice: Double,
    val selectedSize: OrderItemSize?,
    val selectedToppings: List<OrderItemTopping>,
    val selectedSpiceLevel: OrderItemSpiceLevel?,
    val quantity: Int,
    val notes: String?,
    val restaurantId: String?
) {
    fun calculateItemTotal(): Double {
        val sizePrice = selectedSize?.additionalPrice ?: 0.0
        val toppingsPrice = selectedToppings.sumOf { it.price }
        return (basePrice + sizePrice + toppingsPrice) * quantity
    }

    fun toOrderItemRequest(): OrderItemRequest {
        return OrderItemRequest(
            menuItemId = menuItemId,
            name = name,
            image = image,
            basePrice = basePrice,
            quantity = quantity,
            customizations = OrderItemCustomization(
                size = selectedSize,
                toppings = selectedToppings,
                spiceLevel = selectedSpiceLevel
            ),
            itemTotal = calculateItemTotal(),
            notes = notes
        )
    }
}