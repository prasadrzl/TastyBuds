package com.app.tastybuds.data.model.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import com.app.tastybuds.domain.model.DiscountType
import com.app.tastybuds.domain.model.Voucher
import com.app.tastybuds.data.model.VoucherApiResponse
import com.app.tastybuds.domain.model.VoucherType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)
fun VoucherApiResponse.toVoucherDomainModel(restaurantName: String? = null): Voucher {

    @RequiresApi(Build.VERSION_CODES.O)
    fun parseCustomDate(dateString: String?): LocalDateTime? {
        if (dateString.isNullOrEmpty()) return null

        return try {
            val cleanedDate = dateString
                .replace("+00", "")
                .replace("T", " ")

            val patterns = listOf(
                "yyyy-MM-dd HH:mm:ss.SSSSSS",
                "yyyy-MM-dd HH:mm:ss.SSS",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss"
            )

            for (pattern in patterns) {
                try {
                    return LocalDateTime.parse(cleanedDate, DateTimeFormatter.ofPattern(pattern))
                } catch (e: DateTimeParseException) {
                }
            }
            LocalDateTime.parse(dateString.substring(0, 19))
        } catch (e: Exception) {
            null
        }
    }

    val expiryDateTime = parseCustomDate(expiryDate)
    val startDateTime = parseCustomDate(startDate)

    val isExpiredCalculated = expiryDateTime?.let { expiry ->
        expiry.isBefore(LocalDateTime.now())
    } ?: false

    val isStarted = startDateTime?.let { start ->
        start.isBefore(LocalDateTime.now()) || start.isEqual(LocalDateTime.now())
    } ?: true

    val canBeUsedCalculated = isActive &&
            !isUsed &&
            !isExpiredCalculated &&
            isStarted &&
            usedCount < usageLimit

    val remainingUsesCalculated = maxOf(0, usageLimit - usedCount)

    val discountTypeEnum = DiscountType.fromString(discountType)
    val voucherTypeEnum = VoucherType.fromString(voucherType)

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

    val expiryTextGenerated = expiryDateTime?.let { expiry ->
        "Expires ${expiry.format(DateTimeFormatter.ofPattern("MMM dd"))}"
    } ?: "No expiry"

    val minimumOrderTextGenerated = if (minimumOrderAmount > 0) {
        "Min Order $${minimumOrderAmount.toInt()}"
    } else null

    val (buttonTextGenerated, buttonEnabledGenerated) = when {
        isUsed -> "USED" to false
        isExpiredCalculated -> "EXPIRED" to false
        !isStarted -> "NOT STARTED" to false
        !canBeUsedCalculated -> "UNAVAILABLE" to false
        else -> "USE NOW" to true
    }

    val backgroundColorGenerated = when (discountTypeEnum) {
        DiscountType.FREE_DELIVERY -> Color(0xFFFFF3E0)
        DiscountType.PERCENTAGE -> Color(0xFFE8F5E8)
        DiscountType.FIXED_AMOUNT -> Color(0xFFE3F2FD)
        DiscountType.BUY_ONE_GET_ONE -> Color(0xFFF3E5F5)
    }

    val iconColorGenerated = Color.White

    return Voucher(
        id = id,
        userId = userId,
        title = title,
        value = value,
        isUsed = isUsed,
        expiryDate = expiryDate ?: "",
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

@RequiresApi(Build.VERSION_CODES.O)
fun List<VoucherApiResponse>.toVoucherDomainModelList(
    restaurantNames: Map<String, String> = emptyMap()
): List<Voucher> {
    return map { apiModel ->
        val restaurantName = apiModel.restaurantId?.let { restaurantNames[it] }
        apiModel.toVoucherDomainModel(restaurantName)
    }
}