package com.app.tastybuds.ui.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.location.Geocoder
import android.util.Log
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.app.tastybuds.R
import com.app.tastybuds.ui.theme.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
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

    val mapId = remember {
        try {
            context.packageManager
                .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                .metaData
                ?.getString("com.google.android.geo.MAP_ID")
        } catch (e: Exception) {
            Log.e("LocationTracker", "Error getting Map ID from manifest", e)
            null
        }
    }

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
            try {
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
            } catch (e: Exception) {
                Log.e("LocationTracker", "Error getting current location", e)
            }
        }
    }

    LaunchedEffect(cameraPositionState.position.target) {
        val target = cameraPositionState.position.target
        address = getAddressFromLatLng(context, target)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (permissionGranted.value) {
                GoogleMap(
                    modifier = Modifier
                        .fillMaxSize()
                        .semantics {
                            contentDescription = "Map for selecting delivery location"
                        },
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        mapType = MapType.NORMAL,
                        isMyLocationEnabled = true,
                        mapStyleOptions = null // You can add custom styling here if needed
                    ),
                    uiSettings = MapUiSettings(
                        myLocationButtonEnabled = true,
                        zoomControlsEnabled = false,
                        compassEnabled = true,
                        mapToolbarEnabled = false
                    ),
                    // Set the Map ID here for the new renderer
                    googleMapOptionsFactory = {
                        GoogleMapOptions().apply {
                            // Use Map ID if available, otherwise fallback to basic options
                            mapId?.let {
                                mapId(it)
                                Log.d("LocationTracker", "Using Map ID: $it")
                            } ?: run {
                                Log.w("LocationTracker", "Map ID not found, using legacy options")
                                // Fallback options for legacy renderer
                                liteMode(false)
                            }
                        }
                    }
                ) {
                    currentLocation?.let {
                        Marker(
                            state = MarkerState(position = it),
                            icon = bitmapDescriptorFromVector(
                                context,
                                R.drawable.ic_user_location
                            ),
                            title = "Current Location"
                        )
                    }
                }
            } else {
                PermissionRequiredContent()
            }

            CenterPinOverlay(modifier = Modifier.align(Alignment.Center))

            BottomSheetCard(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(Spacing.medium), // Use your theme spacing
                selectedType = selectedType,
                onTypeSelected = { selectedType = it },
                address = address,
                onAddressChange = { address = it },
                onConfirm = onConfirm
            )
        }
    }
}

@Composable
private fun PermissionRequiredContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.large), // Use your theme spacing
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_user_location),
                contentDescription = null,
                modifier = Modifier.size(ComponentSizes.iconLarge), // Use your theme sizes
                tint = primaryColor()
            )

            Spacer(modifier = Modifier.height(Spacing.medium))

            Text(
                text = stringResource(R.string.location_permission_required),
                style = emptyStateTitle(), // Use your theme typography
                color = onBackgroundColor(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.small))

            Text(
                text = stringResource(R.string.location_permission_required_to_display_map),
                style = emptyStateDescription(), // Use your theme typography
                color = textSecondaryColor(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun BottomSheetCard(
    modifier: Modifier,
    selectedType: String,
    address: String,
    onTypeSelected: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = ComponentSizes.cornerRadius, topEnd = ComponentSizes.cornerRadius),
        color = bottomSheetBackgroundColor(),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(Spacing.large)
        ) {
            LocationHeader()

            Spacer(modifier = Modifier.height(Spacing.medium))

            AddressTextField(
                address = address,
                onAddressChange = onAddressChange
            )

            Spacer(modifier = Modifier.height(Spacing.medium))

            HorizontalDivider(
                modifier = Modifier.padding(vertical = Spacing.small),
                color = dividerColor()
            )

            LocationTypeSelector(
                selectedType = selectedType,
                onTypeSelected = onTypeSelected
            )

            Spacer(modifier = Modifier.height(Spacing.large))

            ConfirmButton(onConfirm = onConfirm)
        }
    }
}

@Composable
private fun LocationHeader() {
    Text(
        text = stringResource(R.string.select_location),
        style = sectionTitle(), // Use your theme typography
        color = bottomSheetContentColor()
    )
}

@Composable
private fun AddressTextField(
    address: String,
    onAddressChange: (String) -> Unit
) {
    OutlinedTextField(
        value = address,
        onValueChange = onAddressChange,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Address input field" },
        placeholder = {
            Text(
                text = stringResource(R.string.enter_your_address),
                color = placeholderTextColor()
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit),
                tint = primaryColor()
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = focusedBorderColor(),
            unfocusedBorderColor = unfocusedBorderColor(),
            focusedTextColor = enabledTextColor(),
            unfocusedTextColor = enabledTextColor(),
            cursorColor = primaryColor()
        )
    )
}

@Composable
private fun LocationTypeSelector(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    val locationTypes = listOf(
        stringResource(R.string.home),
        stringResource(R.string.work),
        stringResource(R.string.other)
    )

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        locationTypes.forEach { type ->
            LocationTypeOption(
                type = type,
                isSelected = selectedType == type,
                onTypeSelected = onTypeSelected
            )
        }
    }
}

@Composable
private fun LocationTypeOption(
    type: String,
    isSelected: Boolean,
    onTypeSelected: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onTypeSelected(type) }
            .semantics { contentDescription = "Location type: $type" }
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = radioButtonSelectedColor(),
                unselectedColor = radioButtonUnselectedColor()
            )
        )

        Spacer(modifier = Modifier.width(Spacing.small))

        Text(
            text = type,
            style = bodyMedium().copy( // Use your theme typography
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            ),
            color = if (isSelected) bottomSheetContentColor() else textSecondaryColor()
        )
    }
}

@Composable
private fun ConfirmButton(onConfirm: () -> Unit) {
    Button(
        onClick = onConfirm,
        modifier = Modifier
            .fillMaxWidth()
            .height(ComponentSizes.buttonHeight) // Use your theme sizes
            .semantics { contentDescription = "Confirm location selection" },
        shape = RoundedCornerShape(ComponentSizes.cornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = primaryColor()
        )
    ) {
        Text(
            text = stringResource(R.string.confirm),
            color = onPrimaryColor(),
            style = buttonText() // Use your theme typography
        )
    }
}

@Composable
private fun CenterPinOverlay(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(56.dp)
            .background(
                color = primaryColor(),
                shape = CircleShape
            )
            .semantics { contentDescription = "Location pin marker" }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_user_location),
            contentDescription = null,
            tint = onPrimaryColor(),
            modifier = Modifier.size(ComponentSizes.iconMedium) // Use your theme sizes
        )
    }
}

fun bitmapDescriptorFromVector(
    context: Context,
    @DrawableRes vectorResId: Int
): BitmapDescriptor {
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
        Log.e("LocationTracker", "Error getting address", e)
        context.getString(R.string.unable_to_fetch_address)
    }
}