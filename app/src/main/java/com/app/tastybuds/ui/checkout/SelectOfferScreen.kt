package com.app.tastybuds.ui.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.tastybuds.R
import com.app.tastybuds.ui.theme.focusedBorderColor
import com.app.tastybuds.ui.theme.primaryColor
import com.app.tastybuds.ui.theme.surfaceVariantColor
import com.app.tastybuds.util.ui.AppTopBar

@Composable
fun SelectOfferScreen(offers: List<OfferItem>, onUseNowClick: () -> Unit) {
    var selectedOffer by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppTopBar(title = "Select offer", onBackClick = { })

        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text(stringResource(R.string.add_or_search_for_voucher)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = surfaceVariantColor(),
                focusedContainerColor = surfaceVariantColor(),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = focusedBorderColor()
            )
        )



        LazyColumn(modifier = Modifier.weight(1f)) {
            items(offers) { offer ->

                val isSelected = selectedOffer == offer.id
                val backgroundColor = when {
                    isSelected -> Color(0xFFFFF3E0)
                    offer.enabled -> Color(0xFFFDFDFD)
                    else -> Color(0xFFF2F2F2)
                }

                val borderStroke = when {
                    isSelected -> BorderStroke(1.dp, Color(0xFFFF7700))
                    offer.enabled -> BorderStroke(1.dp, Color(0xFFE0E0E0))
                    else -> null
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = backgroundColor,
                    border = borderStroke
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = offer.enabled) { selectedOffer = offer.id }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = offer.iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = offer.label,
                            color = if (offer.enabled) Color.Black else Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (offer.enabled) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedOffer = offer.id },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFF7700))
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = onUseNowClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor()),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Use now", color = Color.White)
        }
    }
}

data class OfferItem(
    val id: String,
    val label: String,
    val iconRes: Int,
    val enabled: Boolean = true
)

@Preview(showBackground = true)
@Composable
fun PreviewSelectOfferScreen() {
    val offers = listOf(
        OfferItem("1", "- 10%", R.drawable.ic_offer_percentage, true),
        OfferItem("2", "-$1 shipping fee", R.drawable.ic_shiping_fee, true),
        OfferItem("3", "-10% for E-wallet", R.drawable.ic_ewallet, true),
        OfferItem("4", "- 30% for bill over $50", R.drawable.ic_more_offer_percentage, false),
        OfferItem("5", "Freeship", R.drawable.ic_free_shiping, false)
    )
    SelectOfferScreen(offers = offers, onUseNowClick = {})
}
