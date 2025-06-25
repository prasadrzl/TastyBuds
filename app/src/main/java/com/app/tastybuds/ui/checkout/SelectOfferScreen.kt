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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.tastybuds.R
import com.app.tastybuds.ui.theme.primaryColor
import com.app.tastybuds.ui.theme.primaryContainerColor
import com.app.tastybuds.ui.theme.surfaceColor
import com.app.tastybuds.ui.theme.surfaceVariantColor
import com.app.tastybuds.ui.theme.onSurfaceColor
import com.app.tastybuds.ui.theme.textSecondaryColor
import com.app.tastybuds.ui.theme.textDisabledColor
import com.app.tastybuds.ui.theme.borderColor
import com.app.tastybuds.ui.theme.borderFocusColor
import com.app.tastybuds.ui.theme.onPrimaryColor
import com.app.tastybuds.ui.theme.Spacing
import com.app.tastybuds.ui.theme.ComponentSizes
import com.app.tastybuds.ui.theme.bodyMedium
import com.app.tastybuds.ui.theme.buttonText
import com.app.tastybuds.ui.theme.inputLabel
import com.app.tastybuds.util.ui.AppTopBar

object OfferScreenDimensions {
    val offerIconSize = 60.dp
    val offerItemCornerRadius = 8.dp
    val offerItemSpacing = 6.dp
    val offerItemContentPadding = 16.dp
    val offerIconSpacing = 12.dp
}

@Composable
fun SelectOfferScreen(
    offers: List<OfferItem>,
    onUseNowClick: () -> Unit
) {
    var selectedOffer by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor())
    ) {
        AppTopBar(
            title = stringResource(R.string.select_offer),
            onBackClick = { }
        )

        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = {
                Text(
                    text = stringResource(R.string.add_or_search_for_voucher),
                    style = inputLabel(),
                    color = textSecondaryColor()
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(R.string.cd_search)
                )
            },
            modifier = Modifier
                .padding(
                    horizontal = Spacing.medium,
                    vertical = Spacing.small
                )
                .fillMaxWidth(),
            shape = RoundedCornerShape(ComponentSizes.cornerRadius),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = surfaceVariantColor(),
                focusedContainerColor = surfaceVariantColor(),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = borderFocusColor()
            )
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(offers) { offer ->
                val isSelected = selectedOffer == offer.id
                val backgroundColor = when {
                    isSelected -> primaryContainerColor()
                    offer.enabled -> surfaceColor()
                    else -> surfaceVariantColor()
                }

                val borderStroke = when {
                    isSelected -> BorderStroke(ComponentSizes.strokeWidth, primaryColor())
                    offer.enabled -> BorderStroke(ComponentSizes.strokeWidth, borderColor())
                    else -> null
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = Spacing.medium,
                            vertical = OfferScreenDimensions.offerItemSpacing
                        ),
                    shape = RoundedCornerShape(OfferScreenDimensions.offerItemCornerRadius),
                    color = backgroundColor,
                    border = borderStroke
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = offer.enabled) {
                                selectedOffer = offer.id
                            }
                            .padding(OfferScreenDimensions.offerItemContentPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = offer.iconRes),
                            contentDescription = stringResource(R.string.voucher),
                            modifier = Modifier.size(OfferScreenDimensions.offerIconSize)
                        )

                        Spacer(modifier = Modifier.width(OfferScreenDimensions.offerIconSpacing))

                        Text(
                            text = offer.label,
                            style = bodyMedium(),
                            color = if (offer.enabled) onSurfaceColor() else textDisabledColor()
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        if (offer.enabled) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedOffer = offer.id },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = primaryColor()
                                )
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
                .padding(Spacing.medium)
                .height(ComponentSizes.buttonHeight),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor()
            ),
            shape = RoundedCornerShape(ComponentSizes.cornerRadius)
        ) {
            Text(
                text = stringResource(R.string.use_now),
                style = buttonText(),
                color = onPrimaryColor()
            )
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