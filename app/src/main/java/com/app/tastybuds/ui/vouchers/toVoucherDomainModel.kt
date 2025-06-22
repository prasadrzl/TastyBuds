package com.app.tastybuds.ui.vouchers

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)
fun VoucherApiResponse.toVoucherDomainModel(restaurantName: String? = null): Voucher {
    // Parse expiry date and calculate if expired
    val isExpiredCalculated = try {
        val expiryDateTime = LocalDateTime.parse(
            expiryDate.replace("T", " ").replace("+00:00", ""),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        )
        expiryDateTime.isBefore(LocalDateTime.now())
    } catch (e: DateTimeParseException) {
        try {
            val expiryDateTime = LocalDateTime.parse(
                expiryDate.substring(0, 19),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
            )
            expiryDateTime.isBefore(LocalDateTime.now())
        } catch (e2: DateTimeParseException) {
            false
        }
    }

    // Parse start date and check if started
    val isStarted = try {
        val startDateTime = LocalDateTime.parse(
            startDate.replace("T", " ").replace("+00:00", ""),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
        )
        startDateTime.isBefore(LocalDateTime.now())
    } catch (e: DateTimeParseException) {
        true // Default to started if parsing fails
    }

    // Calculate if voucher can be used
    val canBeUsedCalculated = isActive && 
                             !isUsed && 
                             !isExpiredCalculated && 
                             isStarted && 
                             usedCount < usageLimit

    val remainingUsesCalculated = maxOf(0, usageLimit - usedCount)

    val discountTypeEnum = DiscountType.fromString(discountType)
    val voucherTypeEnum = VoucherType.fromString(voucherType)

    // Generate UI-friendly text
    val discountTextGenerated = when (discountTypeEnum) {
        DiscountType.PERCENTAGE -> "${value.toInt()}% Off"
        DiscountType.FIXED_AMOUNT -> "$${value.toInt()} Off"
        DiscountType.FREE_DELIVERY -> "Free Delivery"
        DiscountType.BUY_ONE_GET_ONE -> "Buy 1 Get 1"
    }

    val iconTextGenerated = when (discountTypeEnum) {
        DiscountType.PERCENTAGE -> "${value.toInt()}% OFF"
        DiscountType.FIXED_AMOUNT -> "$${value.toInt()} OFF"
        DiscountType.FREE_DELIVERY -> "FREE"
        DiscountType.BUY_ONE_GET_ONE -> "BOGO"
    }

    val validityTextGenerated = when {
        restaurantName != null -> "Valid at: $restaurantName"
        voucherTypeEnum == VoucherType.GLOBAL -> "Valid at: All Restaurants"
        else -> "Valid at: Selected Restaurants"
    }

    val expiryTextGenerated = try {
        val expiryDateTime = LocalDateTime.parse(
            expiryDate.substring(0, 19),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        )
        "Expires ${expiryDateTime.format(DateTimeFormatter.ofPattern("MMM dd"))}"
    } catch (e: DateTimeParseException) {
        "Expires Soon"
    }

    val minimumOrderTextGenerated = if (minimumOrderAmount > 0) {
        "Min Order $${minimumOrderAmount.toInt()}"
    } else null

    val (buttonTextGenerated, buttonEnabledGenerated) = when {
        isUsed -> "USED" to false
        isExpiredCalculated -> "EXPIRED" to false
        !canBeUsedCalculated -> "UNAVAILABLE" to false
        else -> "USE NOW" to true
    }

    // Set background and icon colors based on discount type
    val backgroundColorGenerated = when (discountTypeEnum) {
        DiscountType.FREE_DELIVERY -> Color(0xFFFFF3E0) // Orange/cream
        DiscountType.PERCENTAGE -> Color(0xFFE8F5E8) // Light green
        DiscountType.FIXED_AMOUNT -> Color(0xFFE3F2FD) // Light blue
        DiscountType.BUY_ONE_GET_ONE -> Color(0xFFF3E5F5) // Light purple
    }

    val iconColorGenerated = Color.White

    return Voucher(
        id = id,
        userId = userId,
        title = title,
        value = value,
        isUsed = isUsed,
        expiryDate = expiryDate,
        createdAt = createdAt,
        restaurantId = restaurantId,
        restaurantName = restaurantName,
        voucherType = voucherTypeEnum,
        applicableCategoryIds = applicableCategoryIds,
        minimumOrderAmount = minimumOrderAmount,
        usageLimit = usageLimit,
        usedCount = usedCount,
        isActive = isActive,
        startDate = startDate,
        endDate = endDate,
        description = description.ifEmpty { discountTextGenerated },
        discountType = discountTypeEnum,
        backgroundColor = backgroundColorGenerated,
        isExpired = isExpiredCalculated,
        canBeUsed = canBeUsedCalculated,
        remainingUses = remainingUsesCalculated,
        expiryText = expiryTextGenerated,
        discountText = discountTextGenerated,
        validityText = validityTextGenerated,
        buttonText = buttonTextGenerated,
        buttonEnabled = buttonEnabledGenerated,
        iconText = iconTextGenerated,
        iconColor = iconColorGenerated,
        minimumOrderText = minimumOrderTextGenerated
    )
}

fun List<VoucherApiResponse>.toVoucherDomainModelList(
    restaurantNames: Map<String, String> = emptyMap()
): List<Voucher> {
    return map { apiModel ->
        val restaurantName = apiModel.restaurantId?.let { restaurantNames[it] }
        apiModel.toVoucherDomainModel(restaurantName)
    }
}