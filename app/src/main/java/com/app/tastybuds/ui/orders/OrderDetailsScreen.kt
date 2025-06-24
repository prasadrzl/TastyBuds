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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.app.tastybuds.ui.theme.*
import com.app.tastybuds.util.formatOrderDate
import com.app.tastybuds.util.ui.AppTopBar
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

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
        containerColor = backgroundColor()
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
                color = loadingIndicatorColor(),
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = stringResource(R.string.loading_order_details),
                style = MaterialTheme.typography.bodyLarge,
                color = onBackgroundColor()
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
                tint = errorColor(),
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = stringResource(R.string.failed_to_load_order),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = onBackgroundColor()
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = onSurfaceVariantColor(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor())
            ) {
                Text(
                    text = stringResource(R.string.try_again),
                    color = onPrimaryColor()
                )
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
        colors = CardDefaults.cardColors(containerColor = surfaceColor()),
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
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = onSurfaceColor()
                    )
                    Text(
                        text = formatOrderDate(order.createdAt),
                        style = MaterialTheme.typography.bodyMedium,
                        color = onSurfaceVariantColor()
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
        colors = CardDefaults.cardColors(containerColor = surfaceColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.restaurant),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = textSecondaryColor()
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
                        .background(surfaceVariantColor()),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = stringResource(R.string.restaurant),
                        tint = onSurfaceVariantColor(),
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.restaurantName ?: stringResource(R.string.restaurant_name),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = onSurfaceColor()
                    )

                    Text(
                        text = stringResource(R.string.restaurant_fast_food),
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurfaceVariantColor()
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = stringResource(R.string.rating),
                            tint = starRatingColor(),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.time_place_holder),
                            style = MaterialTheme.typography.bodySmall,
                            color = onSurfaceVariantColor(),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                OutlinedButton(
                    onClick = onContactRestaurant,
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = primaryColor()
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, primaryColor())
                ) {
                    Text(
                        text = stringResource(R.string.contact),
                        style = MaterialTheme.typography.labelSmall
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
        colors = CardDefaults.cardColors(containerColor = surfaceColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.order_items, orderItems.size),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = onSurfaceColor()
            )

            Spacer(modifier = Modifier.height(16.dp))

            orderItems.forEach { item ->
                OrderItemRow(item = item)
                if (item != orderItems.last()) {
                    HorizontalDivider(
                        color = outlineVariantColor(),
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
                .background(surfaceVariantColor()),
            contentAlignment = Alignment.Center
        ) {
            if (item.image.isNullOrEmpty()) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = stringResource(R.string.food),
                    tint = onSurfaceVariantColor(),
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
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = onSurfaceColor(),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (item.customizations.size != null) {
                Text(
                    text = "Size: ${item.customizations.size.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurfaceVariantColor(),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            if (item.customizations.toppings.isNotEmpty()) {
                Text(
                    text = stringResource(
                        R.string.toppings_number_holder,
                        item.customizations.toppings.joinToString(", ") { it.name }),
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurfaceVariantColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (item.customizations.spiceLevel != null) {
                Text(
                    text = stringResource(R.string.spice, item.customizations.spiceLevel.name),
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurfaceVariantColor()
                )
            }

            if (!item.notes.isNullOrEmpty()) {
                Text(
                    text = stringResource(R.string.note_number_holder, item.notes),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    color = onSurfaceVariantColor()
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = stringResource(R.string.qty_number_holder, item.quantity),
                style = MaterialTheme.typography.bodySmall,
                color = onSurfaceVariantColor()
            )
            Text(
                text = "$${"%.2f".format(item.itemTotal)}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = primaryColor()
            )
        }
    }
}

@Composable
private fun DeliveryInfoCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.delivery_information),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = textSecondaryColor()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = stringResource(R.string.address),
                    tint = primaryColor(),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = stringResource(R.string.delivery_address),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = onSurfaceColor()
                    )
                    Text(
                        text = order.deliveryAddress.addressLine,
                        style = MaterialTheme.typography.bodyMedium,
                        color = onSurfaceVariantColor(),
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    if (!order.deliveryAddress.deliveryInstructions.isNullOrEmpty()) {
                        Text(
                            text = "Instructions: ${order.deliveryAddress.deliveryInstructions}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            ),
                            color = onSurfaceVariantColor(),
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
        colors = CardDefaults.cardColors(containerColor = surfaceColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.payment_summary),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = onSurfaceColor()
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

            HorizontalDivider(
                color = outlineVariantColor(),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.total),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = onSurfaceColor()
                )
                Text(
                    text = "$${"%.2f".format(order.totalAmount)}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = primaryColor()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = stringResource(R.string.payment),
                    tint = onSurfaceVariantColor(),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Paid via ${order.paymentMethod.replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurfaceVariantColor(),
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
        colors = CardDefaults.cardColors(containerColor = surfaceColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.order_timeline),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = onSurfaceColor()
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
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor()),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = stringResource(R.string.track),
                    tint = onPrimaryColor(),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.track_order),
                    color = onPrimaryColor()
                )
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
            tint = primaryColor(),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = onSurfaceColor(),
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = onSurfaceVariantColor()
        )
    }
}

@Composable
private fun OrderStatusChip(
    status: OrderStatus,
    isLarge: Boolean = false
) {
    val (backgroundColor, textColor) = when (status) {
        OrderStatus.PENDING -> warningContainerColor() to onWarningContainerColor()
        OrderStatus.CONFIRMED -> infoContainerColor() to onInfoContainerColor()
        OrderStatus.PREPARING -> offerBackgroundColor() to offerTextColor()
        OrderStatus.READY -> successContainerColor() to onSuccessContainerColor()
        OrderStatus.OUT_FOR_DELIVERY -> offerBackgroundColor() to offerTextColor()
        OrderStatus.DELIVERED -> successContainerColor() to onSuccessContainerColor()
        OrderStatus.CANCELLED -> errorContainerColor() to onErrorContainerColor()
    }

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(if (isLarge) 16.dp else 12.dp)),
        color = backgroundColor
    ) {
        Text(
            text = status.displayName,
            style = if (isLarge) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.labelSmall,
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
            style = MaterialTheme.typography.bodyMedium,
            color = onSurfaceVariantColor()
        )
        Text(
            text = if (isDiscount) "-$${"%.2f".format(amount)}" else "$${"%.2f".format(amount)}",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDiscount) errorColor() else onSurfaceColor()
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
                        color = if (step.isCompleted) primaryColor() else outlineVariantColor(),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                        .background(outlineVariantColor())
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = if (step.isCompleted) onSurfaceColor() else onSurfaceVariantColor()
            )
            if (step.time.isNotEmpty()) {
                Text(
                    text = step.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurfaceVariantColor()
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