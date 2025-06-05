package com.app.tastybuds.ui.orders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tastybuds.R
import com.app.tastybuds.ui.theme.PrimaryColor
import com.app.tastybuds.util.AppTopBar

// Data Models
data class OrderItem(
    val id: String,
    val name: String,
    val size: String,
    val toppings: List<String>,
    val sauce: String = "",
    val spiciness: String,
    val price: Int,
    val quantity: Int,
    val imageRes: Int
)

data class AlsoOrderedItem(
    val id: String,
    val name: String,
    val price: Int,
    val imageRes: Int
)

data class PaymentMethod(
    val id: String,
    val name: String,
    val iconRes: Int
)

data class OrderSummary(
    val subtotal: Double,
    val deliveryFee: Double,
    val promotion: Double,
    val paymentMethod: String,
    val total: Double
)

@Composable
fun OrderReviewScreen(
    onBackClick: () -> Unit = {},
    onChangeAddress: () -> Unit = {},
    onAddMore: () -> Unit = {},
    onEditItem: (String) -> Unit = {},
    onQuantityChange: (String, Int) -> Unit = { _, _ -> },
    onAlsoOrderedClick: (String) -> Unit = {},
    onPaymentMethodClick: () -> Unit = {},
    onPromotionClick: () -> Unit = {},
    onOrderNow: () -> Unit = {}
) {
    val orderItems = getOrderItems()
    val alsoOrderedItems = getAlsoOrderedItems()
    val orderSummary = getOrderSummary()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Bar
        AppTopBar(
            title = "Order review",
            onBackClick = onBackClick
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                // Delivery Address Section
                DeliveryAddressSection(
                    address = "201 Katlian No.21 Street",
                    deliveryTime = "20 mins",
                    onChangeAddress = onChangeAddress
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                // Order Details Section
                OrderDetailsSection(
                    orderItems = orderItems,
                    onAddMore = onAddMore,
                    onEditItem = onEditItem,
                    onQuantityChange = onQuantityChange
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                // Also Ordered Section
                AlsoOrderedSection(
                    items = alsoOrderedItems,
                    onItemClick = onAlsoOrderedClick
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                // Payment Details Section
                PaymentDetailsSection(
                    orderSummary = orderSummary,
                    onPaymentMethodClick = onPaymentMethodClick,
                    onPromotionClick = onPromotionClick
                )
            }

            item {
                Spacer(modifier = Modifier.height(120.dp)) // Space for bottom button
            }
        }

        // Bottom Order Button
        BottomOrderButton(
            total = orderSummary.total,
            onOrderNow = onOrderNow
        )
    }
}

@Composable
fun DeliveryAddressSection(
    address: String,
    deliveryTime: String,
    onChangeAddress: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Delivered to",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            TextButton(onClick = onChangeAddress) {
                Text(
                    text = "Change address",
                    fontSize = 14.sp,
                    color = PrimaryColor
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_user_location),
                contentDescription = "Location",
                modifier = Modifier.size(20.dp),
                tint = PrimaryColor
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = address,
                fontSize = 16.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back_arrow), // Clock icon
                contentDescription = "Time",
                modifier = Modifier.size(20.dp),
                tint = PrimaryColor
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = deliveryTime,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun OrderDetailsSection(
    orderItems: List<OrderItem>,
    onAddMore: () -> Unit,
    onEditItem: (String) -> Unit,
    onQuantityChange: (String, Int) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Order details",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            TextButton(onClick = onAddMore) {
                Text(
                    text = "Add more",
                    fontSize = 14.sp,
                    color = PrimaryColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        orderItems.forEachIndexed { index, item ->
            OrderItemCard(
                item = item,
                onEditClick = { onEditItem(item.id) },
                onQuantityChange = { newQuantity ->
                    onQuantityChange(item.id, newQuantity)
                }
            )

            if (index < orderItems.size - 1) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFFE0E0E0)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun OrderItemCard(
    item: OrderItem,
    onEditClick: () -> Unit,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Food Image
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = item.name,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Item Details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = item.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Size: ${item.size}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            if (item.toppings.isNotEmpty()) {
                Text(
                    text = "Topping: ${item.toppings.joinToString(", ")}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            if (item.sauce.isNotEmpty()) {
                Text(
                    text = "Sauce: ${item.sauce}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = "Spiciness: ${item.spiciness}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${item.price}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Quantity Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { 
                            if (item.quantity > 1) {
                                onQuantityChange(item.quantity - 1)
                            }
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                    ) {
                        Text(
                            text = "−",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }

                    Text(
                        text = item.quantity.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = { onQuantityChange(item.quantity + 1) },
                        modifier = Modifier
                            .size(32.dp)
                            .background(PrimaryColor, RoundedCornerShape(6.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlsoOrderedSection(
    items: List<AlsoOrderedItem>,
    onItemClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Also ordered",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                AlsoOrderedItemCard(
                    item = item,
                    onClick = { onItemClick(item.id) }
                )
            }
        }
    }
}

@Composable
fun AlsoOrderedItemCard(
    item: AlsoOrderedItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = item.name,
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = item.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = "$${item.price}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun PaymentDetailsSection(
    orderSummary: OrderSummary,
    onPaymentMethodClick: () -> Unit,
    onPromotionClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Payment details",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Payment Method Row
        PaymentRow(
            icon = R.drawable.ic_ewallet,
            title = "E-wallet",
            onClick = onPaymentMethodClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider(
            thickness = 1.dp,
            color = Color(0xFFE0E0E0)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Promotion Row
        PaymentRow(
            icon = R.drawable.ic_offer_percentage,
            title = "- 30% for bill over $50",
            onClick = onPromotionClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Order Summary
        OrderSummarySection(orderSummary = orderSummary)
    }
}

@Composable
fun PaymentRow(
    icon: Int,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.Black
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Arrow",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun OrderSummarySection(orderSummary: OrderSummary) {
    Column {
        SummaryRow(
            label = "Subtotal",
            value = "$${orderSummary.subtotal.toInt()}"
        )

        Spacer(modifier = Modifier.height(8.dp))

        SummaryRow(
            label = "Delivery fee",
            value = "$${orderSummary.deliveryFee.toInt()}"
        )

        Spacer(modifier = Modifier.height(8.dp))

        SummaryRow(
            label = "Promotion",
            value = "-$${orderSummary.promotion}",
            valueColor = PrimaryColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            thickness = 1.dp,
            color = Color(0xFFE0E0E0)
        )

        Spacer(modifier = Modifier.height(16.dp))

        SummaryRow(
            label = "Payment method",
            value = orderSummary.paymentMethod
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            thickness = 2.dp,
            color = Color(0xFFE0E0E0)
        )

        Spacer(modifier = Modifier.height(16.dp))

        SummaryRow(
            label = "Total",
            value = "$${orderSummary.total}",
            labelWeight = FontWeight.Bold,
            valueWeight = FontWeight.Bold,
            valueSize = 20.sp
        )
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String,
    labelWeight: FontWeight = FontWeight.Normal,
    valueWeight: FontWeight = FontWeight.Normal,
    valueColor: Color = Color.Black,
    valueSize: androidx.compose.ui.unit.TextUnit = 16.sp
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = labelWeight,
            color = Color.Gray
        )

        Text(
            text = value,
            fontSize = valueSize,
            fontWeight = valueWeight,
            color = valueColor
        )
    }
}

@Composable
fun BottomOrderButton(
    total: Double,
    onOrderNow: () -> Unit
) {
    Button(
        onClick = onOrderNow,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
        shape = RoundedCornerShape(28.dp)
    ) {
        Text(
            text = "Order now",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

// Dummy Data Functions
private fun getOrderItems(): List<OrderItem> {
    return listOf(
        OrderItem(
            id = "1",
            name = "Fried Chicken",
            size = "L",
            toppings = listOf("Corn", "Cheese Cheddar"),
            spiciness = "Hot",
            price = 32,
            quantity = 1,
            imageRes = R.drawable.default_food
        ),
        OrderItem(
            id = "2",
            name = "Chicken Salad",
            size = "M",
            toppings = emptyList(),
            sauce = "Roasted Sesame",
            spiciness = "No",
            price = 10,
            quantity = 1,
            imageRes = R.drawable.default_food
        )
    )
}

private fun getAlsoOrderedItems(): List<AlsoOrderedItem> {
    return listOf(
        AlsoOrderedItem(
            id = "1",
            name = "Sauté Chicken Rice",
            price = 15,
            imageRes = R.drawable.default_food
        ),
        AlsoOrderedItem(
            id = "2",
            name = "Spicy Noodles",
            price = 12,
            imageRes = R.drawable.default_food
        )
    )
}

private fun getOrderSummary(): OrderSummary {
    return OrderSummary(
        subtotal = 32.0,
        deliveryFee = 2.0,
        promotion = 3.2,
        paymentMethod = "E-wallet",
        total = 30.8
    )
}

@Preview(showBackground = true)
@Composable
fun OrderReviewScreenPreview() {
    OrderReviewScreen()
}