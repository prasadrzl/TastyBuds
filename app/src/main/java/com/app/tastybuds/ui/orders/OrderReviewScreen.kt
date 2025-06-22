package com.app.tastybuds.ui.orders

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.tastybuds.R
import com.app.tastybuds.data.model.CartItem
import com.app.tastybuds.data.model.OrderItemSize
import com.app.tastybuds.data.model.OrderItemSpiceLevel
import com.app.tastybuds.data.model.OrderItemTopping
import com.app.tastybuds.data.model.RestaurantMenuItem
import com.app.tastybuds.data.model.UserAddress
import com.app.tastybuds.data.model.Voucher
import com.app.tastybuds.ui.theme.PrimaryColor
import com.app.tastybuds.util.ui.AppTopBar
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@Composable
fun OrderReviewScreen(
    cartItems: List<CartItem>,
    onBackClick: () -> Unit = {},
    onOrderSuccess: (String) -> Unit = {},
    onChangeAddress: () -> Unit = {},
    onSelectOffer: () -> Unit = {},
    onAddMore: (String?) -> Unit = {},
    onEditItem: (CartItem) -> Unit = {},
    viewModel: OrderReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current


    LaunchedEffect(cartItems) {
        if (cartItems.isNotEmpty()) {
            viewModel.loadOrderReviewData(cartItems)
        }
    }

    LaunchedEffect(uiState.orderCreated) {
        if (uiState.orderCreated && uiState.createdOrderId != null) {
            onOrderSuccess(uiState.createdOrderId!!)
            viewModel.resetOrderCreatedState()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppTopBar(
            title = stringResource(R.string.order_review),
            onBackClick = onBackClick
        )

        when {
            uiState.isLoading -> {
                LoadingContent()
            }

            uiState.cartItems.isEmpty() -> {
                EmptyCartContent(onBackClick = onBackClick)
            }

            else -> {
                OrderReviewContent(
                    uiState = uiState,
                    onChangeAddress = onChangeAddress,
                    onSelectOffer = onSelectOffer,
                    onQuantityChange = viewModel::updateItemQuantity,
                    onRemoveItem = viewModel::removeItem,
                    onOrderNow = {
                        Toast.makeText(
                            context,
                            context.getString(R.string.e_wallet_payment_is_under_development),
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.createOrder()
                    },
                    estimatedDeliveryTime = viewModel.getEstimatedDeliveryTime(),
                    isCreatingOrder = uiState.isCreatingOrder,
                    onAddMore = onAddMore,
                    onEditItem = onEditItem
                )
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
        CircularProgressIndicator(color = PrimaryColor)
    }
}

@Composable
private fun EmptyCartContent(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = stringResource(R.string.empty_cart),
            modifier = Modifier.size(80.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.your_cart_is_empty),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.add_some_delicious_items_to_your_cart),
            fontSize = 14.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onBackClick,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
        ) {
            Text(stringResource(R.string.browse_menu), color = Color.White)
        }
    }
}

@Composable
private fun OrderReviewContent(
    uiState: OrderReviewUiState,
    onChangeAddress: () -> Unit,
    onSelectOffer: () -> Unit,
    onQuantityChange: (CartItem, Int) -> Unit,
    onRemoveItem: (CartItem) -> Unit,
    onOrderNow: () -> Unit,
    onAddMore: (String?) -> Unit,
    onEditItem: (CartItem) -> Unit,
    estimatedDeliveryTime: String,
    isCreatingOrder: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DeliveryAddressSection(
                address = uiState.userAddress,
                deliveryTime = estimatedDeliveryTime,
                onChangeAddress = onChangeAddress
            )
        }

        item {
            OrderDetailsSection(
                cartItems = uiState.cartItems,
                onQuantityChange = onQuantityChange,
                onRemoveItem = onRemoveItem,
                onAddMore = {
                    val restaurantId = uiState.cartItems.firstOrNull()?.restaurantId
                    onAddMore(restaurantId)
                },
                onEditItem = onEditItem
            )
        }

        if (uiState.recommendedItems.isNotEmpty()) {
            item {
                AlsoOrderedSection(
                    recommendedItems = uiState.recommendedItems
                )
            }
        }

        item {
            PaymentDetailsSection(
                subtotal = uiState.subtotal,
                deliveryFee = uiState.deliveryFee,
                promotionDiscount = uiState.promotionDiscount,
                totalAmount = uiState.totalAmount,
                selectedVoucher = uiState.selectedVoucher,
                onSelectOffer = onSelectOffer
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onOrderNow,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(28.dp),
                enabled = !isCreatingOrder && uiState.userAddress != null
            ) {
                if (isCreatingOrder) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.creating_order),
                        color = Color.White,
                        fontSize = 16.sp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.order_now),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun DeliveryAddressSection(
    address: UserAddress?,
    deliveryTime: String,
    onChangeAddress: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.delivered_to),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = stringResource(R.string.change_address),
                fontSize = 14.sp,
                color = PrimaryColor,
                modifier = Modifier.clickable { onChangeAddress() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = stringResource(R.string.location),
                tint = PrimaryColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = address?.addressLine ?: stringResource(R.string.no_address_selected),
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = stringResource(R.string.time),
                tint = PrimaryColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = deliveryTime,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun OrderDetailsSection(
    cartItems: List<CartItem>,
    onQuantityChange: (CartItem, Int) -> Unit,
    onRemoveItem: (CartItem) -> Unit,
    onAddMore: () -> Unit = {},
    onEditItem: (CartItem) -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.order_details),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = stringResource(R.string.add_more),
                fontSize = 14.sp,
                color = PrimaryColor,
                modifier = Modifier.clickable { onAddMore() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        cartItems.forEach { cartItem ->
            OrderItemCard(
                cartItem = cartItem,
                onQuantityChange = { newQuantity -> onQuantityChange(cartItem, newQuantity) },
                onRemove = { onRemoveItem(cartItem) },
                onEdit = { onEditItem(cartItem) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun OrderItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    onEdit: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlideImage(
            model = cartItem.image,
            contentDescription = cartItem.name,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            failure = placeholder(R.drawable.default_food),
            loading = placeholder(R.drawable.default_food)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = cartItem.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            cartItem.selectedSize?.let { size ->
                Text(
                    text = stringResource(R.string.size_brackets, size.name),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            if (cartItem.selectedToppings.isNotEmpty()) {
                val toppingsText = cartItem.selectedToppings.joinToString(", ") { it.name }
                Text(
                    text = stringResource(R.string.topping_brackets, toppingsText),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            cartItem.selectedSpiceLevel?.let { spice ->
                Text(
                    text = stringResource(R.string.spiciness_brackets, spice.name),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$${cartItem.calculateItemTotal()}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit),
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onEdit() },
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        if (cartItem.quantity > 1) {
                            onQuantityChange(cartItem.quantity - 1)
                        } else {
                            onRemove()
                        }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (cartItem.quantity > 1) Icons.Default.Add else Icons.Default.Delete,
                        contentDescription = if (cartItem.quantity > 1) "Decrease" else "Remove",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = cartItem.quantity.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                IconButton(
                    onClick = { onQuantityChange(cartItem.quantity + 1) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = PrimaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun AlsoOrderedSection(
    recommendedItems: List<RestaurantMenuItem>
) {
    Column {
        Text(
            text = "Also ordered",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(recommendedItems) { item ->
                Card(
                    modifier = Modifier
                        .width(120.dp)
                        .clickable { /* Handle item click */ },
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        GlideImage(
                            model = item.imageUrl,
                            contentDescription = item.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentScale = ContentScale.Crop,
                            failure = placeholder(R.drawable.default_food),
                            loading = placeholder(R.drawable.default_food)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = item.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "$${item.price}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentDetailsSection(
    subtotal: Double,
    deliveryFee: Double,
    promotionDiscount: Double,
    totalAmount: Double,
    selectedVoucher: Voucher?,
    onSelectOffer: () -> Unit
) {
    Column {
        Text(
            text = "Payment details",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "E-wallet",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "E-wallet",
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Change",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectOffer() }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Promotion",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = selectedVoucher?.title ?: "- 30% for bill over $50",
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Change",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        PaymentBreakdownRow("Subtotal", subtotal)
        PaymentBreakdownRow("Delivery fee", deliveryFee)
        if (promotionDiscount > 0) {
            PaymentBreakdownRow("Promotion", -promotionDiscount, isDiscount = true)
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.payment_method),
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = stringResource(R.string.e_wallet),
                fontSize = 14.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "$${String.format("%.1f", totalAmount)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun PaymentBreakdownRow(
    label: String,
    amount: Double,
    isDiscount: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = if (isDiscount) "-$${String.format("%.1f", amount.coerceAtLeast(0.0))}"
            else "$${String.format("%.1f", amount)}",
            fontSize = 14.sp,
            color = if (isDiscount) Color.Red else Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OrderReviewScreenPreview() {
    val sampleCartItems = listOf(
        CartItem(
            menuItemId = "menu_001",
            name = "Fried Chicken",
            image = null,
            basePrice = 15.0,
            selectedSize = OrderItemSize("size_large", "L", 10.0),
            selectedToppings = listOf(
                OrderItemTopping("topping_corn", "Corn", 2.0),
                OrderItemTopping("topping_cheese", "Cheese Cheddar", 5.0)
            ),
            selectedSpiceLevel = OrderItemSpiceLevel("spice_hot", "Hot", 3),
            quantity = 1,
            notes = null,
            restaurantId = "rest_001"
        ),
        CartItem(
            menuItemId = "menu_002",
            name = "Chicken Salad",
            image = null,
            basePrice = 10.0,
            selectedSize = OrderItemSize("size_medium", "M", 0.0),
            selectedToppings = emptyList(),
            selectedSpiceLevel = null,
            quantity = 1,
            notes = null,
            restaurantId = "rest_001"
        )
    )

    OrderReviewScreen(
        cartItems = sampleCartItems
    )
}