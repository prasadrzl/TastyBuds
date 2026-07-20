package com.app.tastybuds.data.model.mapper

import com.app.tastybuds.data.model.DeliveryAddress as DeliveryAddressDto
import com.app.tastybuds.data.model.Order as OrderDto
import com.app.tastybuds.data.model.OrderItemCustomization as OrderItemCustomizationDto
import com.app.tastybuds.data.model.OrderItemRequest
import com.app.tastybuds.data.model.OrderItemSize as OrderItemSizeDto
import com.app.tastybuds.data.model.OrderItemSpiceLevel as OrderItemSpiceLevelDto
import com.app.tastybuds.data.model.OrderItemTopping as OrderItemToppingDto
import com.app.tastybuds.data.model.OrderStatus as OrderStatusDto
import com.app.tastybuds.data.model.UserAddress as UserAddressDto
import com.app.tastybuds.data.model.Voucher as VoucherDto
import com.app.tastybuds.domain.model.AvailableVoucher
import com.app.tastybuds.domain.model.DeliveryAddress
import com.app.tastybuds.domain.model.Order
import com.app.tastybuds.domain.model.OrderItemCustomization
import com.app.tastybuds.domain.model.OrderItemSize
import com.app.tastybuds.domain.model.OrderItemSpiceLevel
import com.app.tastybuds.domain.model.OrderItemTopping
import com.app.tastybuds.domain.model.OrderLineItem
import com.app.tastybuds.domain.model.OrderStatus
import com.app.tastybuds.domain.model.UserAddress

fun OrderStatusDto.toDomain(): OrderStatus = OrderStatus.fromString(name.lowercase())

fun UserAddressDto.toDomain(): UserAddress = UserAddress(
    id = id,
    userId = userId,
    addressLine = addressLine,
    street = street,
    city = city,
    state = state,
    postalCode = postalCode,
    country = country,
    latitude = latitude,
    longitude = longitude,
    addressType = addressType,
    addressLabel = addressLabel,
    isDefault = isDefault,
    deliveryInstructions = deliveryInstructions,
    createdAt = createdAt
)

fun DeliveryAddressDto.toDomain(): DeliveryAddress = DeliveryAddress(
    addressLine = addressLine,
    street = street,
    city = city,
    state = state,
    postalCode = postalCode,
    country = country,
    latitude = latitude,
    longitude = longitude,
    deliveryInstructions = deliveryInstructions
)

fun OrderItemSizeDto.toDomain(): OrderItemSize = OrderItemSize(
    id = id,
    name = name,
    additionalPrice = additionalPrice
)

fun OrderItemToppingDto.toDomain(): OrderItemTopping = OrderItemTopping(
    id = id,
    name = name,
    price = price
)

fun OrderItemSpiceLevelDto.toDomain(): OrderItemSpiceLevel = OrderItemSpiceLevel(
    id = id,
    name = name,
    level = level
)

fun OrderItemCustomizationDto.toDomain(): OrderItemCustomization = OrderItemCustomization(
    size = size?.toDomain(),
    toppings = toppings.map { it.toDomain() },
    spiceLevel = spiceLevel?.toDomain()
)

fun OrderItemRequest.toDomain(): OrderLineItem = OrderLineItem(
    menuItemId = menuItemId,
    name = name,
    image = image,
    basePrice = basePrice,
    quantity = quantity,
    customizations = customizations.toDomain(),
    itemTotal = itemTotal,
    notes = notes
)

fun OrderDto.toDomain(): Order = Order(
    id = id,
    userId = userId,
    restaurantId = restaurantId,
    restaurantName = restaurantName,
    status = status.toDomain(),
    orderItems = orderItems.map { it.toDomain() },
    subtotal = subtotal,
    deliveryFee = deliveryFee,
    promotionDiscount = promotionDiscount,
    totalAmount = totalAmount,
    paymentMethod = paymentMethod,
    paymentStatus = paymentStatus,
    deliveryAddress = deliveryAddress.toDomain(),
    deliveryLat = deliveryLat,
    deliveryLng = deliveryLng,
    estimatedDeliveryTime = estimatedDeliveryTime,
    actualDeliveryTime = actualDeliveryTime,
    specialNotes = specialNotes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun VoucherDto.toDomain(): AvailableVoucher = AvailableVoucher(
    id = id,
    userId = userId,
    title = title,
    description = description,
    discountType = discountType,
    discountValue = discountValue,
    minimumOrder = minimumOrder,
    maxDiscount = maxDiscount,
    restaurantId = restaurantId,
    validFrom = validFrom,
    validUntil = validUntil,
    isUsed = isUsed,
    termsConditions = termsConditions
)
