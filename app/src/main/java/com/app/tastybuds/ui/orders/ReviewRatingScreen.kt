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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.Scaffold
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
import com.app.tastybuds.ui.theme.addToCartButtonColor
import com.app.tastybuds.ui.theme.addToCartButtonTextColor
import com.app.tastybuds.ui.theme.backgroundColor
import com.app.tastybuds.ui.theme.enabledTextColor
import com.app.tastybuds.ui.theme.focusedBorderColor
import com.app.tastybuds.ui.theme.onBackgroundColor
import com.app.tastybuds.ui.theme.onPrimaryColor
import com.app.tastybuds.ui.theme.onSurfaceVariantColor
import com.app.tastybuds.ui.theme.placeholderTextColor
import com.app.tastybuds.ui.theme.primaryColor
import com.app.tastybuds.ui.theme.reviewStarEmptyColor
import com.app.tastybuds.ui.theme.reviewStarFilledColor
import com.app.tastybuds.ui.theme.surfaceVariantColor
import com.app.tastybuds.ui.theme.textSecondaryColor
import com.app.tastybuds.ui.theme.unfocusedBorderColor
import com.app.tastybuds.util.ui.AppTopBar

@Composable
fun ReviewRatingScreen(
    onBackClick: () -> Unit = {},
    onSubmitReview: (Int, String, Set<String>) -> Unit = { _, _, _ -> }
) {
    var rating by remember { mutableIntStateOf(4) }
    var feedback by remember { mutableStateOf("") }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        topBar = {
            AppTopBar(title = stringResource(R.string.rating), onBackClick = onBackClick)
        },
        containerColor = backgroundColor()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(32.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            val starIcon: ImageVector =
                ImageVector.vectorResource(id = R.drawable.material_starpurple500_sharp)

            Row {
                repeat(5) { index ->
                    val isSelected = index < rating
                    val icon = if (isSelected) Icons.Default.Star else starIcon
                    val tint = if (isSelected) reviewStarFilledColor() else reviewStarEmptyColor()

                    Icon(
                        imageVector = icon,
                        contentDescription = "Star ${index + 1}",
                        tint = tint,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { rating = index + 1 }
                            .padding(2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.leave_your_feedback_here),
                color = textSecondaryColor(),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            FeedbackChipGroup(
                selectedTags = selectedTags,
                onTagsChanged = { selectedTags = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.care_to_share_more),
                fontWeight = FontWeight.SemiBold,
                color = onBackgroundColor(),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = focusedBorderColor(),
                    unfocusedBorderColor = unfocusedBorderColor(),
                    focusedContainerColor = surfaceVariantColor(),
                    unfocusedContainerColor = surfaceVariantColor(),
                    focusedTextColor = enabledTextColor(),
                    unfocusedTextColor = enabledTextColor()
                ),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    onSubmitReview(rating, feedback, selectedTags)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = addToCartButtonColor(),
                    contentColor = addToCartButtonTextColor()
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.submit),
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FeedbackChipGroup(
    selectedTags: Set<String>,
    onTagsChanged: (Set<String>) -> Unit
) {
    val tags = listOf("Service", "Supportive", "Friendly", "Delivery", "Contactless")

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        tags.forEach { tag ->
            val isSelected = tag in selectedTags

            FilterChip(
                selected = isSelected,
                onClick = {
                    val newTags = if (isSelected) {
                        selectedTags - tag
                    } else {
                        selectedTags + tag
                    }
                    onTagsChanged(newTags)
                },
                label = {
                    Text(
                        text = tag,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                    )
                },
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null,
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = primaryColor(),
                    selectedLabelColor = onPrimaryColor(),
                    selectedLeadingIconColor = onPrimaryColor(),
                    containerColor = surfaceVariantColor(),
                    labelColor = onSurfaceVariantColor()
                ),
                border = FilterChipDefaults.filterChipBorder(
                    selectedBorderColor = primaryColor(),
                    borderColor = Color.Transparent,
                    selectedBorderWidth = 1.dp,
                    enabled = false,
                    selected = false
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RatingScreenPreview() {
    ReviewRatingScreen()
}