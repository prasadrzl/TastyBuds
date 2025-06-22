package com.app.tastybuds.ui.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.app.tastybuds.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.tasks.await
import java.util.Locale

@Preview
@Composable
fun LocationTrackerScreen(onConfirm: () -> Unit = {}) {
    UserLocationMapView(onConfirm)
}

@Composable
fun UserLocationMapView(onConfirm: () -> Unit) {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val cameraPositionState = rememberCameraPositionState()
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var address by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Home") }

    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    val permissionGranted = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                locationPermission
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted.value = granted
    }

    LaunchedEffect(permissionGranted.value) {
        if (!permissionGranted.value) {
            launcher.launch(locationPermission)
        } else {
            val location = fusedLocationClient
                .getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    CancellationTokenSource().token
                )
                .await()

            val latLng = LatLng(location.latitude, location.longitude)
            currentLocation = latLng
            address = getAddressFromLatLng(context, latLng)
            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        }
    }

    LaunchedEffect(cameraPositionState.position.target) {
        val target = cameraPositionState.position.target
        address = getAddressFromLatLng(context, target)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (permissionGranted.value) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                currentLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        icon = bitmapDescriptorFromVector(
                            context,
                            R.drawable.ic_user_location
                        )
                    )
                }
            }
        } else {
            Text(stringResource(R.string.location_permission_required_to_display_map))
        }

        CenterPinOverlay(modifier = Modifier.align(Alignment.Center))

        BottomSheetCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            selectedType = selectedType,
            onTypeSelected = { selectedType = it },
            address = address,
            onAddressChange = { address = it },
            onConfirm = onConfirm
        )
    }
}

@Composable
fun BottomSheetCard(
    modifier: Modifier,
    selectedType: String,
    address: String,
    onTypeSelected: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    Surface(
        modifier,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(R.string.select_location), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = address,
                onValueChange = onAddressChange,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(id = R.string.edit)
                    )
                },
                shape = RoundedCornerShape(8.dp)
            )
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(
                    stringResource(R.string.home),
                    stringResource(R.string.work),
                    stringResource(R.string.other)
                ).forEach {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onTypeSelected(it) }
                    ) {
                        RadioButton(
                            selected = selectedType == it,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFF7700))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(it)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7700))
            ) {
                Text(stringResource(R.string.confirm), color = Color.White)
            }
        }
    }
}

fun bitmapDescriptorFromVector(context: Context, @DrawableRes vectorResId: Int): BitmapDescriptor {
    val drawable = AppCompatResources.getDrawable(context, vectorResId)!!
    val size = (32 * context.resources.displayMetrics.density).toInt() // 32dp
    drawable.setBounds(0, 0, size, size)

    val bitmap = createBitmap(size, size)
    val canvas = Canvas(bitmap)
    drawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}


fun getAddressFromLatLng(context: Context, latLng: LatLng): String {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        address?.firstOrNull()?.getAddressLine(0) ?: context.getString(R.string.location_not_found)
    } catch (e: Exception) {
        context.getString(R.string.unable_to_fetch_address)
    }
}

@Composable
fun CenterPinOverlay(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(56.dp)
            .background(color = Color(0xFFFF7700), shape = CircleShape)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_user_location),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
