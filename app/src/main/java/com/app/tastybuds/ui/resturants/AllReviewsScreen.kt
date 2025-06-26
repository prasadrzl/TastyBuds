package com.app.tastybuds.ui.resturants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tastybuds.R
import com.app.tastybuds.data.model.RestaurantReview
import com.app.tastybuds.ui.theme.backgroundColor
import com.app.tastybuds.ui.theme.cardBackgroundColor
import com.app.tastybuds.ui.theme.cardContentColor
import com.app.tastybuds.ui.theme.onBackgroundColor
import com.app.tastybuds.ui.theme.onPrimaryColor
import com.app.tastybuds.ui.theme.primaryColor
import com.app.tastybuds.ui.theme.starRatingColor
import com.app.tastybuds.ui.theme.textSecondaryColor
import com.app.tastybuds.util.ui.AppTopBar

@Composable
fun AllReviewsScreen(
    reviews: List<RestaurantReview>,
    restaurantName: String = "",
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = if (restaurantName.isNotEmpty()) {
                    stringResource(R.string.reviews_for, restaurantName)
                } else {
                    stringResource(R.string.all_reviews)
                }, onBackClick = onBackClick
            )
        },
        containerColor = backgroundColor()
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.reviews_count, reviews.size),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = textSecondaryColor(),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(reviews) { review ->
                FullWidthReviewCard(review = review)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun FullWidthReviewCard(review: RestaurantReview) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                primaryColor(),
                                RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.userName.take(1).uppercase(),
                            color = onPrimaryColor(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = review.userName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = onBackgroundColor()
                        )

                        Text(
                            text = review.timeAgo,
                            fontSize = 12.sp,
                            color = textSecondaryColor()
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        modifier = Modifier.size(16.dp),
                        tint = starRatingColor()
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = review.rating.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = onBackgroundColor()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = review.comment,
                fontSize = 14.sp,
                color = cardContentColor(),
                lineHeight = 20.sp
            )

            if (review.helpfulCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.helpful_count, review.helpfulCount),
                    fontSize = 12.sp,
                    color = textSecondaryColor()
                )
            }
        }
    }
}