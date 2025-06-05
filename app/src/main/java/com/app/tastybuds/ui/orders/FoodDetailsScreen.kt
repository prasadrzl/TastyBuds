package com.app.tastybuds.ui.orders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tastybuds.R
import com.app.tastybuds.ui.theme.PrimaryColor

data class FoodItem(
    val id: String,
    val name: String,
    val description: String,
    val basePrice: Double,
    val imageRes: Int
)

data class SizeOption(
    val id: String,
    val name: String,
    val additionalPrice: Double
)

data class ToppingOption(
    val id: String,
    val name: String,
    val price: Double,
    var isSelected: Boolean = false
)

data class SpiceLevel(
    val id: String,
    val name: String
)

@Composable
fun FoodDetailsScreen(
    foodItem: FoodItem = getFoodItem(),
    onBackClick: () -> Unit = {},
    onAddToCart: (Double, Int) -> Unit = { _, _ -> }
) {
    var selectedSize by remember { mutableStateOf("L") }
    var selectedToppings by remember { mutableStateOf(mutableListOf("corn", "cheese")) }
    var selectedSpice by remember { mutableStateOf("hot") }
    var quantity by remember { mutableIntStateOf(1) }
    var specialNote by remember { mutableStateOf("") }

    val sizeOptions = getSizeOptions()
    val toppingOptions = getToppingOptions()
    val spiceLevels = getSpiceLevels()

    val totalPrice = calculateTotalPrice(
        foodItem.basePrice,
        selectedSize,
        selectedToppings,
        sizeOptions,
        toppingOptions,
        quantity
    )

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                FoodImageHeader(
                    imageRes = foodItem.imageRes,
                    onCloseClick = onBackClick
                )
            }

            item {
                FoodInfoCard(
                    foodItem = foodItem
                )
            }

            item {
                SizeSelectionSection(
                    selectedSize = selectedSize,
                    sizeOptions = sizeOptions,
                    onSizeSelected = { selectedSize = it }
                )
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    thickness = 1.dp,
                    color = Color(0xFFE0E0E0)
                )
            }

            item {
                ToppingsSection(
                    toppingOptions = toppingOptions,
                    selectedToppings = selectedToppings,
                    onToppingToggled = { toppingId ->
                        selectedToppings = selectedToppings.toMutableList().apply {
                            if (contains(toppingId)) {
                                remove(toppingId)
                            } else {
                                add(toppingId)
                            }
                        }
                    }
                )
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    thickness = 1.dp,
                    color = Color(0xFFE0E0E0)
                )
            }

            item {
                SpicinessSection(
                    selectedSpice = selectedSpice,
                    spiceLevels = spiceLevels,
                    onSpiceSelected = { selectedSpice = it }
                )
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
                    note = specialNote,
                    onNoteChange = { specialNote = it }
                )
            }

            item {
                Spacer(modifier = Modifier.height(120.dp)) // Space for bottom controls
            }
        }

        BottomControls(
            quantity = quantity,
            onQuantityChange = { quantity = it },
            totalPrice = totalPrice,
            onAddToCart = { onAddToCart(totalPrice, quantity) },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun FoodImageHeader(
    imageRes: Int,
    onCloseClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Food Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

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
    }
}

@Composable
fun FoodInfoCard(foodItem: FoodItem) {
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
                    text = foodItem.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = foodItem.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$${foodItem.basePrice.toInt()}",
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

fun calculateTotalPrice(
    basePrice: Double,
    selectedSize: String,
    selectedToppings: List<String>,
    sizeOptions: List<SizeOption>,
    toppingOptions: List<ToppingOption>,
    quantity: Int
): Double {
    val sizePrice = sizeOptions.find { it.id == selectedSize }?.additionalPrice ?: 0.0
    val toppingsPrice = toppingOptions.filter { selectedToppings.contains(it.id) }
        .sumOf { it.price }

    return (basePrice + sizePrice + toppingsPrice) * quantity
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
    totalPrice: Double,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
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

private fun getFoodItem(): FoodItem {
    return FoodItem(
        id = "1",
        name = "Fried Chicken",
        description = "Crispy fried wings, thigh",
        basePrice = 15.0,
        imageRes = R.drawable.default_food // Replace with actual food image
    )
}

private fun getSizeOptions(): List<SizeOption> {
    return listOf(
        SizeOption("S", "S", 0.0),
        SizeOption("M", "M", 5.0),
        SizeOption("L", "L", 10.0)
    )
}

private fun getToppingOptions(): List<ToppingOption> {
    return listOf(
        ToppingOption("corn", "Corn", 2.0),
        ToppingOption("cheese", "Cheese Cheddar", 5.0),
        ToppingOption("salted_egg", "Salted egg", 10.0)
    )
}

private fun getSpiceLevels(): List<SpiceLevel> {
    return listOf(
        SpiceLevel("no", "No"),
        SpiceLevel("hot", "Hot"),
        SpiceLevel("very_hot", "Very hot")
    )
}

@Preview(showBackground = true)
@Composable
fun FoodDetailsScreenPreview() {
    FoodDetailsScreen()
}