package com.app.tastybuds.ui.orders

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.tastybuds.R
import com.app.tastybuds.domain.model.CartItem
import com.app.tastybuds.domain.model.FoodDetails
import com.app.tastybuds.domain.model.SizeOption
import com.app.tastybuds.domain.model.SpiceLevel
import com.app.tastybuds.domain.model.ToppingOption
import com.app.tastybuds.ui.resturants.FoodDetailsViewModel
import com.app.tastybuds.ui.resturants.state.FoodDetailsUiState
import com.app.tastybuds.ui.theme.addToCartButtonColor
import com.app.tastybuds.ui.theme.addToCartButtonTextColor
import com.app.tastybuds.ui.theme.cardBackgroundColor
import com.app.tastybuds.ui.theme.cardContentColor
import com.app.tastybuds.ui.theme.checkboxSelectedColor
import com.app.tastybuds.ui.theme.checkboxUnselectedColor
import com.app.tastybuds.ui.theme.dividerColor
import com.app.tastybuds.ui.theme.enabledTextColor
import com.app.tastybuds.ui.theme.focusedBorderColor
import com.app.tastybuds.ui.theme.heartFavoriteColor
import com.app.tastybuds.ui.theme.onPrimaryColor
import com.app.tastybuds.ui.theme.placeholderTextColor
import com.app.tastybuds.ui.theme.priceTextColor
import com.app.tastybuds.ui.theme.primaryColor
import com.app.tastybuds.ui.theme.quantityButtonBackgroundColor
import com.app.tastybuds.ui.theme.quantityButtonTextColor
import com.app.tastybuds.ui.theme.radioButtonSelectedColor
import com.app.tastybuds.ui.theme.radioButtonUnselectedColor
import com.app.tastybuds.ui.theme.scrimColor
import com.app.tastybuds.ui.theme.textSecondaryColor
import com.app.tastybuds.ui.theme.unfocusedBorderColor
import com.app.tastybuds.util.createCartItemFromUiState
import com.app.tastybuds.util.ui.ErrorScreen
import com.app.tastybuds.util.ui.LoadingScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@Composable
fun FoodDetailsScreen(
    foodItemId: String = "",
    onBackClick: () -> Unit = {},
    onAddToCart: (CartItem) -> Unit = {},
    viewModel: FoodDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cartViewModel: CartViewModel = hiltViewModel()
    val editingItem: CartItem? by cartViewModel.editingItem.collectAsState()

    LaunchedEffect(editingItem, uiState.foodDetailsData) {
        editingItem?.let {
            if (uiState.foodDetailsData != null) {
                it.selectedSize?.let { size ->
                    viewModel.updateSelectedSize(size.id)
                }

                val toppingIds = it.selectedToppings.map { topping -> topping.id }
                viewModel.updateSelectedToppings(toppingIds)

                it.selectedSpiceLevel?.let { spice ->
                    viewModel.updateSelectedSpiceLevel(spice.id)
                }

                viewModel.updateQuantity(it.quantity)
                viewModel.updateSpecialNote(it.notes ?: "")
            }
        }
    }

    LaunchedEffect(foodItemId) {
        viewModel.loadFoodDetails(foodItemId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                LoadingScreen()
            }

            uiState.error != null -> {
                ErrorScreen(
                    title = uiState.error ?: stringResource(R.string.unknown_error),
                    onRetryClick = { viewModel.retry() },
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
                    onAddToCart = { cartItem ->
                        if (editingItem != null) {
                            // Update existing item
                            cartViewModel.updateCartItem(editingItem!!, cartItem)
                            onBackClick() // Go back to order review
                        } else {
                            cartViewModel.addToCart(cartItem)
                            onAddToCart(cartItem)
                        }
                    },
                    onFavoriteClick = { viewModel.toggleFavorite() }
                )
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
    onAddToCart: (CartItem) -> Unit,
    onFavoriteClick: () -> Unit
) {
    val foodData = uiState.foodDetailsData!!


    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 180.dp)
        ) {
            item {
                FoodImageHeader(
                    imageUrl = foodData.foodDetails.imageUrl,
                    onCloseClick = onBackClick,
                    onFavoriteClick = onFavoriteClick,
                    isFavorite = foodData.foodDetails.isFavorite,
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
                    color = dividerColor()
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
                    color = dividerColor()
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
                    color = dividerColor()
                )
            }

            item {
                NoteSection(
                    note = uiState.specialNote,
                    onNoteChange = onNoteChange
                )
            }
        }

        BottomControls(
            quantity = uiState.quantity,
            onQuantityChange = onQuantityChange,
            totalPrice = uiState.totalPrice,
            onAddToCart = onAddToCart,
            createCartItem = {
                createCartItemFromUiState(foodData, uiState)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
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
            contentDescription = stringResource(R.string.food_image),
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
                        color = scrimColor().copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = onPrimaryColor()
                )
            }

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .background(
                        color = scrimColor().copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite)
                        stringResource(R.string.remove_from_favorites)
                    else stringResource(
                        R.string.add_to_favorites
                    ),
                    tint = if (isFavorite) heartFavoriteColor() else onPrimaryColor()
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
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor()),
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
                    color = cardContentColor()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = foodDetails.description,
                    fontSize = 14.sp,
                    color = textSecondaryColor()
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$${foodDetails.basePrice.toInt()}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = priceTextColor()
                )

                Text(
                    text = stringResource(R.string.base_price),
                    fontSize = 12.sp,
                    color = textSecondaryColor()
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
                text = stringResource(R.string.size),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = cardContentColor()
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.pick_1),
                fontSize = 14.sp,
                color = textSecondaryColor()
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = primaryColor(),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.required),
                    tint = onPrimaryColor(),
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
                        selectedColor = radioButtonSelectedColor(),
                        unselectedColor = radioButtonUnselectedColor()
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = size.name,
                    fontSize = 16.sp,
                    color = cardContentColor(),
                    modifier = Modifier.weight(1f)
                )

                if (size.additionalPrice > 0) {
                    Text(
                        text = "+$${size.additionalPrice.toInt()}",
                        fontSize = 14.sp,
                        color = textSecondaryColor()
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
                text = stringResource(R.string.topping),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = cardContentColor()
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.optional),
                fontSize = 14.sp,
                color = textSecondaryColor()
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
                        checkedColor = checkboxSelectedColor(),
                        uncheckedColor = checkboxUnselectedColor()
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = topping.name,
                    fontSize = 16.sp,
                    color = cardContentColor(),
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "+$${topping.price.toInt()}",
                    fontSize = 14.sp,
                    color = textSecondaryColor()
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
                text = stringResource(R.string.spiciness),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = cardContentColor()
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.pick_1_brackets),
                fontSize = 14.sp,
                color = textSecondaryColor()
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = primaryColor(),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.required),
                    tint = onPrimaryColor(),
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
                        selectedColor = radioButtonSelectedColor(),
                        unselectedColor = radioButtonUnselectedColor()
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = spice.name,
                    fontSize = 16.sp,
                    color = cardContentColor()
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
            text = stringResource(R.string.note_for_restaurant),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = cardContentColor()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.special_note),
                    color = placeholderTextColor()
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = focusedBorderColor(),
                unfocusedBorderColor = unfocusedBorderColor(),
                focusedTextColor = enabledTextColor(),
                unfocusedTextColor = enabledTextColor(),
                cursorColor = primaryColor()
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
    onAddToCart: (CartItem) -> Unit,
    createCartItem: () -> CartItem?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackgroundColor()),
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
                            .border(1.dp, unfocusedBorderColor(), RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = "−",
                            fontSize = 20.sp,
                            color = textSecondaryColor()
                        )
                    }

                    Text(
                        text = quantity.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = cardContentColor(),
                        modifier = Modifier.padding(horizontal = 24.dp),
                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = { onQuantityChange(quantity + 1) },
                        modifier = Modifier
                            .size(40.dp)
                            .background(quantityButtonBackgroundColor(), RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add),
                            tint = quantityButtonTextColor()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val cartItem = createCartItem()
                        if (cartItem != null) {
                            onAddToCart(cartItem)
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.please_select_required_options_size_and_spiciness),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = addToCartButtonColor()),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = stringResource(R.string.add_to_cart_brackets, totalPrice.toInt()),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = addToCartButtonTextColor()
                    )
                }
            }
        }
    }
}