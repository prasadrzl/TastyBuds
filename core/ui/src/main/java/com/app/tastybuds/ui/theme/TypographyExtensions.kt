package com.app.tastybuds.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

@Composable
fun appTitle(): TextStyle = MaterialTheme.typography.displayLarge.copy(
    fontFamily = Poppins,
    fontWeight = FontWeight.Bold,
    fontSize = 36.sp
)

@Composable
fun screenTitle(): TextStyle = MaterialTheme.typography.headlineLarge.copy(
    fontFamily = Poppins,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp
)

@Composable
fun sectionTitle(): TextStyle = MaterialTheme.typography.titleLarge.copy(
    fontFamily = Poppins,
    fontWeight = FontWeight.Bold,
    fontSize = 22.sp
)

@Composable
fun cardTitle(): TextStyle = MaterialTheme.typography.titleMedium.copy(
    fontFamily = Poppins,
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp
)

@Composable
fun subTitle(): TextStyle = MaterialTheme.typography.titleSmall.copy(
    fontFamily = Poppins,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp
)

@Composable
fun bodyLarge(): TextStyle = MaterialTheme.typography.bodyLarge.copy(
    fontFamily = Inter,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp
)

@Composable
fun bodyMedium(): TextStyle = MaterialTheme.typography.bodyMedium.copy(
    fontFamily = Inter,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp
)

@Composable
fun bodySmall(): TextStyle = MaterialTheme.typography.bodySmall.copy(
    fontFamily = Inter,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp
)

@Composable
fun bodyBold(): TextStyle = bodyMedium().copy(
    fontWeight = FontWeight.Bold
)

@Composable
fun bodyMediumBold(): TextStyle = bodyMedium().copy(
    fontWeight = FontWeight.Medium
)

@Composable
fun bodySecondary(): TextStyle = bodySmall().copy(
    fontWeight = FontWeight.Normal
)

@Composable
fun restaurantName(): TextStyle = cardTitle().copy(
    fontWeight = FontWeight.SemiBold
)

@Composable
fun restaurantCuisine(): TextStyle = bodySmall().copy(
    fontWeight = FontWeight.Normal
)

@Composable
fun restaurantRating(): TextStyle = MaterialTheme.typography.labelSmall.copy(
    fontFamily = Inter,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp
)

@Composable
fun restaurantDeliveryTime(): TextStyle = restaurantRating()

@Composable
fun restaurantDescription(): TextStyle = bodyMedium()

@Composable
fun foodItemName(): TextStyle = subTitle().copy(
    fontWeight = FontWeight.Medium
)

@Composable
fun foodItemDescription(): TextStyle = bodySmall()

@Composable
fun foodItemPrice(): TextStyle = MaterialTheme.typography.titleMedium.copy(
    fontFamily = Poppins,
    fontWeight = FontWeight.Bold,
    fontSize = 16.sp
)

@Composable
fun foodItemOriginalPrice(): TextStyle = bodyMedium().copy(
    textDecoration = TextDecoration.LineThrough
)

@Composable
fun foodItemQuantity(): TextStyle = bodySmall()

@Composable
fun orderItemName(): TextStyle = bodyMedium().copy(
    fontWeight = FontWeight.Medium
)

@Composable
fun orderItemDetails(): TextStyle = bodySmall()

@Composable
fun orderItemNote(): TextStyle = bodySmall().copy(
    fontStyle = FontStyle.Italic
)

@Composable
fun orderTotal(): TextStyle = MaterialTheme.typography.titleMedium.copy(
    fontFamily = Poppins,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp
)

@Composable
fun orderSubtotal(): TextStyle = bodyMedium().copy(
    fontWeight = FontWeight.Medium
)

@Composable
fun buttonText(): TextStyle = MaterialTheme.typography.labelLarge.copy(
    fontFamily = Poppins,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp
)

@Composable
fun buttonTextLarge(): TextStyle = buttonText().copy(
    fontSize = 16.sp,
    fontWeight = FontWeight.Bold
)

@Composable
fun buttonTextSmall(): TextStyle = buttonText().copy(
    fontSize = 12.sp
)

@Composable
fun badgeText(): TextStyle = MaterialTheme.typography.labelSmall.copy(
    fontFamily = Inter,
    fontWeight = FontWeight.Medium,
    fontSize = 10.sp
)

@Composable
fun discountBadge(): TextStyle = badgeText().copy(
    fontWeight = FontWeight.Bold
)

@Composable
fun popularBadge(): TextStyle = badgeText()

@Composable
fun newBadge(): TextStyle = badgeText()

@Composable
fun statusLabel(): TextStyle = MaterialTheme.typography.labelMedium.copy(
    fontFamily = Inter,
    fontWeight = FontWeight.Medium,
    fontSize = 11.sp
)

@Composable
fun inputLabel(): TextStyle = bodyMedium().copy(
    fontWeight = FontWeight.Medium
)

@Composable
fun inputText(): TextStyle = bodyMedium()

@Composable
fun inputHint(): TextStyle = bodyMedium()

@Composable
fun errorText(): TextStyle = bodySmall().copy(
    fontWeight = FontWeight.Medium
)

@Composable
fun navigationLabel(): TextStyle = MaterialTheme.typography.labelMedium.copy(
    fontFamily = Inter,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp
)

@Composable
fun topBarTitle(): TextStyle = MaterialTheme.typography.titleMedium.copy(
    fontFamily = Poppins,
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp
)

@Composable
fun searchPlaceholder(): TextStyle = bodyMedium()

@Composable
fun searchText(): TextStyle = bodyMedium()

@Composable
fun caption(): TextStyle = MaterialTheme.typography.bodySmall.copy(
    fontFamily = Inter,
    fontWeight = FontWeight.Normal,
    fontSize = 11.sp
)

@Composable
fun timestampText(): TextStyle = caption()

@Composable
fun addressText(): TextStyle = bodySmall()

@Composable
fun linkText(): TextStyle = bodyMedium().copy(
    fontWeight = FontWeight.Medium,
    textDecoration = TextDecoration.Underline
)

@Composable
fun profileName(): TextStyle = MaterialTheme.typography.titleMedium.copy(
    fontFamily = Poppins,
    fontWeight = FontWeight.Bold,
    fontSize = 16.sp
)

@Composable
fun profileEmail(): TextStyle = bodySmall()

@Composable
fun profileMenuOption(): TextStyle = bodyMedium().copy(
    fontWeight = FontWeight.Normal
)

@Composable
fun dialogTitle(): TextStyle = MaterialTheme.typography.headlineSmall.copy(
    fontFamily = Poppins,
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp
)

@Composable
fun dialogBody(): TextStyle = bodyMedium()

@Composable
fun dialogButton(): TextStyle = buttonText().copy(
    fontWeight = FontWeight.Bold
)

@Composable
fun emptyStateTitle(): TextStyle = MaterialTheme.typography.titleLarge.copy(
    fontFamily = Poppins,
    fontWeight = FontWeight.Medium,
    fontSize = 20.sp
)

@Composable
fun emptyStateDescription(): TextStyle = bodyMedium()
