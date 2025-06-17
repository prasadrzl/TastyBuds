package com.app.tastybuds.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.tastybuds.R
import com.app.tastybuds.domain.model.FoodDetails
import com.app.tastybuds.domain.model.SizeOption
import com.app.tastybuds.domain.model.SpiceLevel
import com.app.tastybuds.domain.model.ToppingOption
import com.app.tastybuds.ui.resturants.FoodDetailsViewModel
import com.app.tastybuds.ui.resturants.state.FoodDetailsUiState
import com.app.tastybuds.ui.theme.PrimaryColor
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@Composable
fun FoodDetailsScreen(
    foodItemId: String = "",
    onBackClick: () -> Unit = {},
    onAddToCart: (Float, Int) -> Unit = { _, _ -> },
    viewModel: FoodDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(foodItemId) {
        viewModel.loadFoodDetails(foodItemId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                LoadingContent()
            }

            uiState.error != null -> {
                ErrorContent(
                    error = uiState.error!!,
                    onRetry = { viewModel.retry() },
                    onBackClick = onBackClick
                )
            }

            uiState.foodDetailsData != null -> {
                FoodDetailsContent(
                    uiState = uiState,
                    onBackClick = onBackClick,
                    onSizeSelected = { viewModel.updateSelectedSize(it) },
                    onToppingToggled = { toppingId ->
                        val currentToppings = uiState.selectedToppings.toMutableList()
                        if (currentToppings.contains(toppingId)) {
                            currentToppings.remove(toppingId)
                        } else {
                            currentToppings.add(toppingId)
                        }
                        viewModel.updateSelectedToppings(currentToppings)
                    },
                    onSpiceSelected = { viewModel.updateSelectedSpiceLevel(it) },
                    onNoteChange = { viewModel.updateSpecialNote(it) },
                    onQuantityChange = { viewModel.updateQuantity(it) },
                    onAddToCart = { onAddToCart(uiState.totalPrice, uiState.quantity) },
                    onFavoriteClick = { viewModel.toggleFavorite() },
                    onComboClick = {}
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
            Text(text = "Error: $error")
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                OutlinedButton(onClick = onBackClick) {
                    Text("Go Back")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
private fun FoodDetailsContent(
    uiState: FoodDetailsUiState,
    onBackClick: () -> Unit,
    onSizeSelected: (String) -> Unit,
    onToppingToggled: (String) -> Unit,
    onSpiceSelected: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onQuantityChange: (Int) -> Unit,
    onAddToCart: () -> Unit,
    onFavoriteClick: () -> Unit,
    onComboClick: (String) -> Unit
) {
    val foodData = uiState.foodDetailsData!!

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            FoodImageHeader(
                imageUrl = foodData.foodDetails.imageUrl,
                onCloseClick = onBackClick,
                onFavoriteClick = onFavoriteClick,
                isFavorite = false,
            )
        }

        item {
            FoodInfoCard(
                foodDetails = foodData.foodDetails
            )
        }

        if (foodData.customization.sizes.isNotEmpty()) {
            item {
                SizeSelectionSection(
                    selectedSize = uiState.selectedSize,
                    sizeOptions = foodData.customization.sizes,
                    onSizeSelected = onSizeSelected
                )
            }
        }

        item {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                thickness = 1.dp,
                color = Color(0xFFE0E0E0)
            )
        }

        if (foodData.customization.toppings.isNotEmpty()) {
            item {
                ToppingsSection(
                    toppingOptions = foodData.customization.toppings,
                    selectedToppings = uiState.selectedToppings,
                    onToppingToggled = onToppingToggled
                )
            }
        }

        item {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                thickness = 1.dp,
                color = Color(0xFFE0E0E0)
            )
        }

        if (foodData.customization.spiceLevels.isNotEmpty()) {
            item {
                SpicinessSection(
                    selectedSpice = uiState.selectedSpiceLevel,
                    spiceLevels = foodData.customization.spiceLevels,
                    onSpiceSelected = onSpiceSelected
                )
            }
        }

        item {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                thickness = 1.dp,
                color = Color(0xFFE0E0E0)
            )
        }

        item {
            NoteSection(
                note = uiState.specialNote,
                onNoteChange = onNoteChange
            )
        }

        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }

    BottomControls(
        quantity = uiState.quantity,
        onQuantityChange = onQuantityChange,
        totalPrice = uiState.totalPrice,
        onAddToCart = onAddToCart,
        modifier = Modifier
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FoodImageHeader(
    imageUrl: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        GlideImage(
            model = imageUrl,
            contentDescription = "Food Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            failure = placeholder(R.drawable.default_food),
            loading = placeholder(R.drawable.default_food)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            IconButton(
                onClick = onCloseClick,
                modifier = Modifier
                    .padding(16.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) Color.Red else Color.White
                )
            }
        }
    }
}

@Composable
fun FoodInfoCard(foodDetails: FoodDetails) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-20).dp),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = foodDetails.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = foodDetails.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$${foodDetails.basePrice.toInt()}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "Base price",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun SizeSelectionSection(
    selectedSize: String,
    sizeOptions: List<SizeOption>,
    onSizeSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Size",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "(Pick 1)",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = PrimaryColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Required",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        sizeOptions.forEach { size ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSizeSelected(size.id) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedSize == size.id,
                    onClick = { onSizeSelected(size.id) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = PrimaryColor,
                        unselectedColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = size.name,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )

                if (size.additionalPrice > 0) {
                    Text(
                        text = "+$${size.additionalPrice.toInt()}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun ToppingsSection(
    toppingOptions: List<ToppingOption>,
    selectedToppings: List<String>,
    onToppingToggled: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Topping",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "(Optional)",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        toppingOptions.forEach { topping ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToppingToggled(topping.id) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selectedToppings.contains(topping.id),
                    onCheckedChange = { onToppingToggled(topping.id) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = PrimaryColor,
                        uncheckedColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = topping.name,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "+$${topping.price.toInt()}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun SpicinessSection(
    selectedSpice: String,
    spiceLevels: List<SpiceLevel>,
    onSpiceSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Spiciness",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "(Pick 1)",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = PrimaryColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Required",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        spiceLevels.forEach { spice ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSpiceSelected(spice.id) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedSpice == spice.id,
                    onClick = { onSpiceSelected(spice.id) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = PrimaryColor,
                        unselectedColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = spice.name,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun NoteSection(
    note: String,
    onNoteChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Note for restaurant",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            placeholder = {
                Text(
                    text = "Special note",
                    color = Color.Gray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                cursorColor = PrimaryColor
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
fun BottomControls(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    totalPrice: Float,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
                        modifier = Modifier
                            .size(40.dp)
                            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = "−",
                            fontSize = 20.sp,
                            color = Color.Gray
                        )
                    }

                    Text(
                        text = quantity.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = { onQuantityChange(quantity + 1) },
                        modifier = Modifier
                            .size(40.dp)
                            .background(PrimaryColor, RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onAddToCart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = "Add to cart ($${totalPrice.toInt()})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}