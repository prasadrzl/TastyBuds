package com.app.tastybuds.ui.location

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tastybuds.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Preview(showBackground = true)
@Composable
fun OrderTrackingScreen() {
    val context = LocalContext.current
    val sourceLatLng = LatLng(1.390355, 103.895824) // Example
    val destinationLatLng = LatLng(1.393762, 103.897593) // Example

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(destinationLatLng, 15f)
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Polyline(
                points = listOf(sourceLatLng, destinationLatLng),
                color = Color(0xFFFF7700),
                width = 8f
            )

            Marker(
                state = MarkerState(position = destinationLatLng),
                icon = bitmapDescriptorFromVector(context, R.drawable.ic_user_location)
            )

            Marker(
                state = MarkerState(position = sourceLatLng),
                icon = bitmapDescriptorFromVector(context, R.drawable.ic_user_location)
            )
        }

        TrackingBottomSheet(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
fun TrackingBottomSheet(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Delivery Tracking", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_help),
                    contentDescription = null,
                    tint = Color(0xFFFF7700)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Delevery time", color = Color.Gray)
                    Text("15–20 mins", fontWeight = FontWeight.Medium, fontSize = 18.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_user_location),
                    contentDescription = null,
                    tint = Color(0xFFFF7700)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Delivery Address", color = Color.Gray)
                    Text("201 Katlian No.21 Street", fontWeight = FontWeight.Medium, fontSize = 18.sp)
                }
            }

            Divider(Modifier.padding(vertical = 16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFFF7700), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("J", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("John Cooper", fontWeight = FontWeight.Bold)
                        Text("Food Delivery", color = Color.Gray)
                    }
                }

                Row {
                    IconButton(onClick = { /* Call */ }) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFFFF7700))
                    }
                    IconButton(onClick = { /* Chat */ }) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFF7700))
                    }
                }
            }
        }
    }
}

