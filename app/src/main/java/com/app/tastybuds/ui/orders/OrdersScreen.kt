package com.app.tastybuds.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.tastybuds.R
import com.app.tastybuds.data.model.Order
import com.app.tastybuds.data.model.OrderStatus
import com.app.tastybuds.ui.theme.backgroundColor
import com.app.tastybuds.ui.theme.errorContainerColor
import com.app.tastybuds.ui.theme.infoContainerColor
import com.app.tastybuds.ui.theme.loadingIndicatorColor
import com.app.tastybuds.ui.theme.offerBackgroundColor
import com.app.tastybuds.ui.theme.offerTextColor
import com.app.tastybuds.ui.theme.onBackgroundColor
import com.app.tastybuds.ui.theme.onErrorContainerColor
import com.app.tastybuds.ui.theme.onInfoContainerColor
import com.app.tastybuds.ui.theme.onPrimaryColor
import com.app.tastybuds.ui.theme.onSuccessContainerColor
import com.app.tastybuds.ui.theme.onSurfaceColor
import com.app.tastybuds.ui.theme.onSurfaceVariantColor
import com.app.tastybuds.ui.theme.onWarningContainerColor
import com.app.tastybuds.ui.theme.orderTotalTextColor
import com.app.tastybuds.ui.theme.outlineVariantColor
import com.app.tastybuds.ui.theme.primaryColor
import com.app.tastybuds.ui.theme.successContainerColor
import com.app.tastybuds.ui.theme.surfaceColor
import com.app.tastybuds.ui.theme.warningContainerColor
import com.app.tastybuds.util.formatOrderDate
import com.app.tastybuds.util.ui.EmptyOrdersContent
import com.app.tastybuds.util.ui.ErrorScreen
import com.app.tastybuds.util.ui.LoadingScreen
import kotlinx.coroutines.delay

@Composable
fun OrdersScreen(
    onOrderClick: (String) -> Unit = {},
    onTrackOrder: (String) -> Unit = {},
    onReorderClick: (Order) -> Unit = {},
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadUserOrders()
    }

    Scaffold(
        containerColor = backgroundColor()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column {
                when {
                    uiState.isLoading -> {
                        LoadingScreen()
                    }

                    uiState.error != null -> {
                        ErrorScreen(
                            title = uiState.error!!,
                            onRetryClick = { viewModel.loadUserOrders() }
                        )
                    }

                    uiState.orders.isEmpty() -> {
                        EmptyOrdersContent()
                    }

                    else -> {
                        val displayOrders = uiState.filteredOrders ?: uiState.orders
                        OrdersListContent(
                            orders = displayOrders,
                            onOrderClick = onOrderClick,
                            onTrackOrder = onTrackOrder,
                            onReorderClick = onReorderClick,
                            onRefresh = { viewModel.refreshOrders() }
                        )
                    }
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
                text = stringResource(R.string.loading_your_orders),
                style = MaterialTheme.typography.bodyLarge,
                color = onBackgroundColor()
            )
        }
    }
}

@Composable
private fun OrdersListContent(
    orders: List<Order>,
    onOrderClick: (String) -> Unit,
    onTrackOrder: (String) -> Unit,
    onReorderClick: (Order) -> Unit,
    onRefresh: () -> Unit
) {
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            onRefresh()
            delay(1000)
            isRefreshing = false
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        R.string.order_place_holder,
                        orders.size,
                        if (orders.size != 1) "s" else ""
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = onSurfaceVariantColor()
                )

                TextButton(
                    onClick = { isRefreshing = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.refresh),
                        tint = primaryColor(),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.refresh),
                        color = primaryColor(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        items(orders) { order ->
            OrderCard(
                order = order,
                onOrderClick = { onOrderClick(order.id) },
                onTrackOrder = { onTrackOrder(order.id) },
                onReorderClick = { onReorderClick(order) }
            )
        }
    }
}

@Composable
private fun OrderCard(
    order: Order,
    onOrderClick: () -> Unit,
    onTrackOrder: () -> Unit,
    onReorderClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOrderClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(
                            R.string.order_brackets,
                            order.id.take(8).uppercase()
                        ),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = onSurfaceColor()
                    )
                    Text(
                        text = formatOrderDate(order.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurfaceVariantColor()
                    )
                }

                OrderStatusChip(status = order.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = order.restaurantId ?: stringResource(R.string.restaurant),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = onSurfaceColor(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (order.orderItems.isNotEmpty()) {
                val displayItems = order.orderItems.take(2)
                displayItems.forEach { item ->
                    Text(
                        text = "${item.quantity}× ${item.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurfaceVariantColor(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (order.orderItems.size > 2) {
                    Text(
                        text = stringResource(
                            R.string.and_more_item,
                            order.orderItems.size - 2,
                            if (order.orderItems.size - 2 != 1) "s" else ""
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        color = onSurfaceVariantColor()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = outlineVariantColor())

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${"%.2f".format(order.totalAmount)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = orderTotalTextColor()
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isOrderActive(order.status)) {
                        OutlinedButton(
                            onClick = onTrackOrder,
                            modifier = Modifier.height(32.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = primaryColor()
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, primaryColor())
                        ) {
                            Text(
                                text = stringResource(R.string.track),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    Button(
                        onClick = onReorderClick,
                        modifier = Modifier.height(32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor())
                    ) {
                        Text(
                            text = stringResource(R.string.reorder),
                            color = onPrimaryColor(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderStatusChip(status: OrderStatus) {
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
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor),
        color = backgroundColor
    ) {
        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
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

@Preview(showBackground = true)
@Composable
fun OrdersScreenPreview() {
    OrdersScreen()
}