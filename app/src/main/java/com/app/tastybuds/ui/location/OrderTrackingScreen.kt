package com.app.tastybuds.ui.location

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.tastybuds.R
import com.app.tastybuds.ui.orders.OrderTrackingUiState
import com.app.tastybuds.ui.orders.OrderTrackingViewModel
import com.app.tastybuds.ui.theme.PrimaryColor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun OrderTrackingScreen(
    orderId: String,
    onBackClick: () -> Unit = {},
    viewModel: OrderTrackingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(orderId) {
        viewModel.loadOrderDetails(orderId)
    }
    val cameraPositionState = rememberCameraPositionState {
        val location = uiState.deliveryLocation ?: Pair(1.3966, 103.9072)
        position = CameraPosition.fromLatLngZoom(LatLng(location.first, location.second), 15f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                LoadingContent()
            }

            uiState.error != null -> {
                ErrorContent(
                    error = uiState.error!!,
                    onRetry = { viewModel.retry(orderId) },
                    onBackClick = onBackClick
                )
            }

            uiState.order != null -> {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        mapToolbarEnabled = false
                    )
                ) {
                    if (uiState.restaurantLocation != null && uiState.deliveryLocation != null) {
                        val restaurantLatLng = LatLng(
                            uiState.restaurantLocation!!.first,
                            uiState.restaurantLocation!!.second
                        )
                        val deliveryLatLng = LatLng(
                            uiState.deliveryLocation!!.first,
                            uiState.deliveryLocation!!.second
                        )

                        Polyline(
                            points = listOf(restaurantLatLng, deliveryLatLng),
                            color = PrimaryColor,
                            width = 6f
                        )

                        Marker(
                            state = MarkerState(position = restaurantLatLng),
                            title = stringResource(R.string.restaurants),
                            snippet = stringResource(R.string.order_pickup_location),
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                        )

                        Marker(
                            state = MarkerState(position = deliveryLatLng),
                            title = stringResource(R.string.delivery_location),
                            snippet = uiState.deliveryAddress,
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                        )

                        val progress = when (uiState.order?.status?.name) {
                            "PREPARING" -> 0.1f
                            "READY" -> 0.3f
                            "OUT_FOR_DELIVERY" -> 0.7f
                            "DELIVERED" -> 1.0f
                            else -> 0.1f
                        }
                        val driverLocation =
                            calculateDriverPosition(restaurantLatLng, deliveryLatLng, progress)
                        Marker(
                            state = MarkerState(position = driverLocation),
                            title = stringResource(R.string.delivery_location),
                            snippet = stringResource(R.string.current_position),
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                        )
                    } else {
                        val defaultLocation = LatLng(1.3521, 103.8198)
                        Marker(
                            state = MarkerState(position = defaultLocation),
                            title = stringResource(R.string.loading),
                            snippet = stringResource(R.string.fetching_order_location)
                        )
                    }
                }

                TopHeader(onBackClick = onBackClick)

                DeliveryTrackingBottomSheet(
                    uiState = uiState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun TopHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .background(Color.White, CircleShape)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = Color.Black
            )
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = PrimaryColor)
            Spacer(modifier = Modifier.height(16.dp))
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
    onRetry: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = stringResource(R.string.failed_to_load_order),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row {
                OutlinedButton(onClick = onBackClick) {
                    Text(stringResource(R.string.go_back))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    Text(text = stringResource(R.string.retry), color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun DeliveryTrackingBottomSheet(
    uiState: OrderTrackingUiState,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = Color.White,
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.delivery_tracking),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = stringResource(R.string.time),
                    tint = PrimaryColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(R.string.delivery_time),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = if (uiState.estimatedDeliveryTime.isNotEmpty()) {
                            uiState.estimatedDeliveryTime
                        } else {
                            stringResource(R.string.calculating)
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = stringResource(R.string.location),
                    tint = PrimaryColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(R.string.delivery_address),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = if (uiState.deliveryAddress.isNotEmpty()) {
                            uiState.deliveryAddress
                        } else {
                            stringResource(R.string.loading_address)
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        maxLines = 2
                    )
                }
            }

            if (uiState.order != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (uiState.distance.isNotEmpty()) {
                        Column {
                            Text(
                                text = stringResource(R.string.distance),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = uiState.distance,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                    }

                    Column {
                        Text(
                            text = stringResource(R.string.order_total),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "${
                                String.format(
                                    "%.2f",
                                    uiState.order!!.totalAmount
                                )
                            }",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryColor
                        )
                    }

                    Column {
                        Text(
                            text = stringResource(R.string.status),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = uiState.order.status.displayName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = when (uiState.order.status.name) {
                                "DELIVERED" -> Color.Green
                                "CANCELLED" -> Color.Red
                                else -> PrimaryColor
                            }
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 20.dp),
                color = Color.Gray.copy(alpha = 0.2f)
            )

            DriverInfoSection(
                customerName = uiState.customerName,
                orderId = uiState.order?.id ?: ""
            )
        }
    }
}

@Composable
private fun DriverInfoSection(
    customerName: String = stringResource(R.string.customer),
    orderId: String = ""
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(PrimaryColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (customerName.isNotEmpty()) {
                        customerName.first().uppercase()
                    } else {
                        "Prasad"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = if (customerName.isNotEmpty()) {
                        customerName
                    } else {
                        stringResource(R.string.loading)
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = if (orderId.isNotEmpty()) {
                        stringResource(R.string.order, orderId.take(8))
                    } else {
                        stringResource(R.string.food_delivery)
                    },
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        Row {
            IconButton(
                onClick = {},
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call",
                    tint = PrimaryColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(
                onClick = {},
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = stringResource(R.string.rate),
                    tint = PrimaryColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun calculateDriverPosition(start: LatLng, end: LatLng, progress: Float): LatLng {
    val lat = start.latitude + (end.latitude - start.latitude) * progress
    val lng = start.longitude + (end.longitude - start.longitude) * progress
    return LatLng(lat, lng)
}

@Preview(showBackground = true)
@Composable
fun OrderTrackingScreenPreview() {
    OrderTrackingScreen(
        orderId = "order_87653ef8-4779-43c3-9e85-5b2b1e855cfd",
        onBackClick = {}
    )
}