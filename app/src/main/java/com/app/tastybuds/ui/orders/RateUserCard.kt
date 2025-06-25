package com.app.tastybuds.ui.orders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tastybuds.R
import com.app.tastybuds.ui.theme.*
import com.app.tastybuds.util.ui.AppTopBar

@Preview
@Composable
fun RatingScreen() {
    var rating by remember { mutableIntStateOf(4) }
    var feedback by remember { mutableStateOf("") }

    Column {
        AppTopBar()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(backgroundColor())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(64.dp))
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(primaryColor()),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_profile_person),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    colorFilter = ColorFilter.tint(onPrimaryColor())
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.rate_durgaprasad),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = onBackgroundColor()
            )

            Spacer(modifier = Modifier.height(8.dp))

            val starIcon: ImageVector =
                ImageVector.vectorResource(id = R.drawable.material_starpurple500_sharp)

            Row {
                repeat(5) {
                    val icon = if (it < rating) Icons.Default.Star else starIcon
                    val tint = if (it < rating) reviewStarFilledColor() else reviewStarEmptyColor()
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { rating = it + 1 }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = stringResource(R.string.leave_your_feedback_here),
                color = textSecondaryColor()
            )

            Spacer(modifier = Modifier.height(20.dp))

            FeedbackChipGroup()

            Spacer(modifier = Modifier.height(56.dp))

            Text(
                text = stringResource(R.string.care_to_share_more),
                fontWeight = FontWeight.SemiBold,
                color = onBackgroundColor()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = feedback,
                onValueChange = { feedback = it },
                placeholder = {
                    Text(
                        text = stringResource(R.string.leave_feedback_about_driver),
                        color = placeholderTextColor()
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = focusedBorderColor(),
                    unfocusedBorderColor = unfocusedBorderColor(),
                    focusedContainerColor = surfaceVariantColor(),
                    unfocusedContainerColor = surfaceVariantColor(),
                    focusedTextColor = enabledTextColor(),
                    unfocusedTextColor = enabledTextColor()
                )
            )

            Spacer(modifier = Modifier.height(80.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = addToCartButtonColor(),
                    contentColor = addToCartButtonTextColor()
                )
            ) {
                Text("Submit")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FeedbackChipGroup() {
    var selectedTags by remember { mutableStateOf(setOf<String>()) }

    val tags = listOf("Service", "Supportive", "Friendly", "Delivery", "Contactless")

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(top = 16.dp)
    ) {
        tags.forEach { tag ->
            val isSelected = tag in selectedTags

            FilterChip(
                selected = isSelected,
                onClick = {
                    selectedTags = if (isSelected) selectedTags - tag else selectedTags + tag
                },
                label = {
                    Text(
                        text = tag,
                        color = if (isSelected) {
                            when (tag) {
                                "Supportive", "Contactless" -> textSecondaryColor()
                                else -> primaryColor()
                            }
                        } else {
                            onSurfaceVariantColor()
                        },
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = if (isSelected) {
                            when (tag) {
                                "Supportive", "Contactless" -> textSecondaryColor()
                                else -> primaryColor()
                            }
                        } else {
                            onSurfaceVariantColor()
                        },
                        modifier = Modifier.size(16.dp)
                    )
                },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = when (tag) {
                        "Supportive", "Contactless" -> surfaceVariantColor()
                        else -> primaryContainerColor()
                    },
                    containerColor = surfaceVariantColor(),
                    labelColor = onSurfaceVariantColor(),
                    selectedLabelColor = when (tag) {
                        "Supportive", "Contactless" -> textSecondaryColor()
                        else -> primaryColor()
                    }
                )
            )
        }
    }
}