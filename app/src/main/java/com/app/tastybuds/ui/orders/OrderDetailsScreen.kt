package com.app.tastybuds.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.tastybuds.R
import com.app.tastybuds.data.model.Order
import com.app.tastybuds.data.model.OrderItemRequest
import com.app.tastybuds.data.model.OrderStatus
import com.app.tastybuds.ui.theme.PrimaryColor
import com.app.tastybuds.util.formatOrderDate
import com.app.tastybuds.util.ui.AppTopBar
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun OrderDetailsScreen(
    orderId: String,
    onBackClick: () -> Unit = {},
    onTrackOrder: () -> Unit = {},
    onContactRestaurant: () -> Unit = {},
    viewModel: OrderDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(orderId) {
        viewModel.loadOrderDetails(orderId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.order_details),
                onBackClick = onBackClick,
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingContent()
                }

                uiState.error != null -> {
                    ErrorContent(
                        error = uiState.error!!,
                        onRetry = { viewModel.loadOrderDetails(orderId) }
                    )
                }

                uiState.order != null -> {
                    OrderDetailsContent(
                        order = uiState.order!!,
                        onTrackOrder = onTrackOrder,
                        onContactRestaurant = onContactRestaurant
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = PrimaryColor,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = stringResource(R.string.loading_order_details),
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.error),
                tint = Color.Red,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = stringResource(R.string.failed_to_load_order),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = error,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text(stringResource(R.string.try_again))
            }
        }
    }
}

@Composable
private fun OrderDetailsContent(
    order: Order,
    onTrackOrder: () -> Unit,
    onContactRestaurant: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OrderHeaderCard(order = order)
        }

        item {
            RestaurantInfoCard(order = order, onContactRestaurant = onContactRestaurant)
        }

        item {
            OrderItemsCard(orderItems = order.orderItems)
        }

        item {
            DeliveryInfoCard(order = order)
        }

        item {
            PaymentSummaryCard(order = order)
        }

        if (order.status != OrderStatus.PENDING) {
            item {
                OrderTimelineCard(order = order)
            }
        }

        item {
            ActionButtonsSection(
                order = order,
                onTrackOrder = onTrackOrder
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun OrderHeaderCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Order #${order.id.take(8).uppercase()}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = formatOrderDate(order.createdAt),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                OrderStatusChip(status = order.status, isLarge = true)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(
                    icon = Icons.Default.ShoppingCart,
                    label = stringResource(R.string.items),
                    value = "${order.orderItems.size}"
                )

                InfoItem(
                    icon = Icons.Default.ShoppingCart,
                    label = stringResource(R.string.total),
                    value = "$${"%.2f".format(order.totalAmount)}"
                )

                InfoItem(
                    icon = Icons.Default.ShoppingCart,
                    label = stringResource(R.string.time),
                    value = getEstimatedTime(order.status)
                )
            }
        }
    }
}

@Composable
private fun RestaurantInfoCard(
    order: Order,
    onContactRestaurant: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.restaurant),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = stringResource(R.string.restaurant),
                        tint = Color.Gray,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.restaurantName ?: stringResource(R.string.restaurant_name),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )

                    Text(
                        text = stringResource(R.string.restaurant_fast_food),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = stringResource(R.string.rating),
                            tint = Color(0xFFFFB000),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.time_place_holder),
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                OutlinedButton(
                    onClick = onContactRestaurant,
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PrimaryColor
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryColor)
                ) {
                    Text(
                        text = stringResource(R.string.contact),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderItemsCard(orderItems: List<OrderItemRequest>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.order_items, orderItems.size),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            orderItems.forEach { item ->
                OrderItemRow(item = item)
                if (item != orderItems.last()) {
                    Divider(
                        color = Color.Gray.copy(alpha = 0.2f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun OrderItemRow(item: OrderItemRequest) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Gray.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            if (item.image.isNullOrEmpty()) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = stringResource(R.string.food),
                    tint = Color.Gray,
                    modifier = Modifier.size(30.dp)
                )
            } else {
                GlideImage(
                    model = item.image,
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    failure = placeholder(R.drawable.default_food)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (item.customizations.size != null) {
                Text(
                    text = "Size: ${item.customizations.size?.name ?: stringResource(R.string.regular)}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            if (item.customizations.toppings.isNotEmpty()) {
                Text(
                    text = stringResource(
                        R.string.toppings_number_holder,
                        item.customizations.toppings.joinToString(", ") { it.name }),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (item.customizations.spiceLevel != null) {
                Text(
                    text = stringResource(R.string.spice, item.customizations.spiceLevel.name),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            if (!item.notes.isNullOrEmpty()) {
                Text(
                    text = stringResource(R.string.note_number_holder, item.notes),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = stringResource(R.string.qty_number_holder, item.quantity),
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = "$${"%.2f".format(item.itemTotal)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        }
    }
}

@Composable
private fun DeliveryInfoCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.delivery_information),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = stringResource(R.string.address),
                    tint = PrimaryColor,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = stringResource(R.string.delivery_address),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = order.deliveryAddress.addressLine,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    if (!order.deliveryAddress.deliveryInstructions.isNullOrEmpty()) {
                        Text(
                            text = "Instructions: ${order.deliveryAddress.deliveryInstructions}",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentSummaryCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.payment_summary),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            PaymentRow(label = "Subtotal", amount = order.subtotal)
            PaymentRow(label = "Delivery Fee", amount = order.deliveryFee)

            if (order.promotionDiscount > 0) {
                PaymentRow(
                    label = "Discount",
                    amount = -order.promotionDiscount,
                    isDiscount = true
                )
            }

            Divider(
                color = Color.Gray.copy(alpha = 0.3f),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.total),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "$${"%.2f".format(order.totalAmount)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = stringResource(R.string.payment),
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Paid via ${order.paymentMethod.replaceFirstChar { it.uppercase() }}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun OrderTimelineCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.order_timeline),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            val timelineSteps = generateTimelineSteps(order)

            timelineSteps.forEachIndexed { index, step ->
                TimelineStep(
                    step = step,
                    isLast = index == timelineSteps.size - 1
                )
            }
        }
    }
}

@Composable
private fun ActionButtonsSection(
    order: Order,
    onTrackOrder: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isOrderActive(order.status)) {
            Button(
                onClick = onTrackOrder,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = stringResource(R.string.track),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.track_order))
            }
        }
    }
}

@Composable
private fun InfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = PrimaryColor,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun OrderStatusChip(
    status: OrderStatus,
    isLarge: Boolean = false
) {
    val (backgroundColor, textColor) = when (status) {
        OrderStatus.PENDING -> Color(0xFFFFF3CD) to Color(0xFF856404)
        OrderStatus.CONFIRMED -> Color(0xFFD1ECF1) to Color(0xFF0C5460)
        OrderStatus.PREPARING -> Color(0xFFFFE6CC) to PrimaryColor
        OrderStatus.READY -> Color(0xFFD4EDDA) to Color(0xFF155724)
        OrderStatus.OUT_FOR_DELIVERY -> Color(0xFFFFE6CC) to PrimaryColor
        OrderStatus.DELIVERED -> Color(0xFFD4EDDA) to Color(0xFF155724)
        OrderStatus.CANCELLED -> Color(0xFFF8D7DA) to Color(0xFF721C24)
    }

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(if (isLarge) 16.dp else 12.dp)),
        color = backgroundColor
    ) {
        Text(
            text = status.displayName,
            fontSize = if (isLarge) 14.sp else 10.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.padding(
                horizontal = if (isLarge) 12.dp else 8.dp,
                vertical = if (isLarge) 8.dp else 4.dp
            )
        )
    }
}

@Composable
private fun PaymentRow(
    label: String,
    amount: Double,
    isDiscount: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = if (isDiscount) "-$${"%.2f".format(amount)}" else "$${"%.2f".format(amount)}",
            fontSize = 14.sp,
            color = if (isDiscount) Color.Red else Color.Black
        )
    }
}

@Composable
private fun TimelineStep(
    step: TimelineStepData,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = if (step.isCompleted) PrimaryColor else Color.Gray.copy(alpha = 0.3f),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                        .background(Color.Gray.copy(alpha = 0.2f))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = step.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (step.isCompleted) Color.Black else Color.Gray
            )
            if (step.time.isNotEmpty()) {
                Text(
                    text = step.time,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

data class TimelineStepData(
    val title: String,
    val time: String,
    val isCompleted: Boolean
)

private fun getEstimatedTime(status: OrderStatus): String {
    return when (status) {
        OrderStatus.PENDING -> "Pending"
        OrderStatus.CONFIRMED -> "30-45 min"
        OrderStatus.PREPARING -> "20-30 min"
        OrderStatus.READY -> "10-15 min"
        OrderStatus.OUT_FOR_DELIVERY -> "5-10 min"
        OrderStatus.DELIVERED -> "Delivered"
        OrderStatus.CANCELLED -> "Cancelled"
    }
}

private fun isOrderActive(status: OrderStatus): Boolean {
    return status in listOf(
        OrderStatus.PENDING,
        OrderStatus.CONFIRMED,
        OrderStatus.PREPARING,
        OrderStatus.READY,
        OrderStatus.OUT_FOR_DELIVERY
    )
}

@Composable
private fun generateTimelineSteps(order: Order): List<TimelineStepData> {
    val steps = mutableListOf<TimelineStepData>()
    val currentStatus = order.status

    steps.add(
        TimelineStepData(
            title = stringResource(R.string.order_placed),
            time = formatOrderDate(order.createdAt),
            isCompleted = true
        )
    )

    if (currentStatus != OrderStatus.PENDING) {
        steps.add(
            TimelineStepData(
                title = stringResource(R.string.order_confirmed),
                time = "2 mins ago",
                isCompleted = true
            )
        )
    }

    if (currentStatus in listOf(
            OrderStatus.PREPARING,
            OrderStatus.READY,
            OrderStatus.OUT_FOR_DELIVERY,
            OrderStatus.DELIVERED
        )
    ) {
        steps.add(
            TimelineStepData(
                title = stringResource(R.string.preparing),
                time = "5 mins ago",
                isCompleted = true
            )
        )
    }

    if (currentStatus in listOf(
            OrderStatus.READY,
            OrderStatus.OUT_FOR_DELIVERY,
            OrderStatus.DELIVERED
        )
    ) {
        steps.add(
            TimelineStepData(
                title = stringResource(R.string.ready_for_pickup),
                time = "Just now",
                isCompleted = true
            )
        )
    }

    if (currentStatus in listOf(OrderStatus.OUT_FOR_DELIVERY, OrderStatus.DELIVERED)) {
        steps.add(
            TimelineStepData(
                title = stringResource(R.string.out_for_delivery),
                time = "In progress",
                isCompleted = currentStatus == OrderStatus.DELIVERED
            )
        )
    }

    if (currentStatus == OrderStatus.DELIVERED) {
        steps.add(
            TimelineStepData(
                title = stringResource(R.string.delivered),
                time = order.actualDeliveryTime ?: "Delivered",
                isCompleted = true
            )
        )
    }

    return steps
}

@Preview(showBackground = true)
@Composable
fun OrderDetailsScreenPreview() {
    OrderDetailsScreen(orderId = "order_123")
}