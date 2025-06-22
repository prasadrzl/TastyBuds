package com.tastybuds.app.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tastybuds.util.ui.OnboardingUtils.markOnboardingCompleted

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 3 })

    val onboardingPages = listOf(
        OnboardingPage(
            title = "Discover Amazing Food",
            description = "Explore thousands of delicious recipes and find your next favorite meal",
            gradientColors = listOf(
                Color(0xFFFF8A80),
                Color(0xFFFF5722),
                Color(0xFFE91E63)
            ),
            icon = "🍽️"
        ),
        OnboardingPage(
            title = "Fast Delivery",
            description = "Get your favorite food delivered hot and fresh right to your doorstep",
            gradientColors = listOf(
                Color(0xFF80CBC4),
                Color(0xFF26A69A),
                Color(0xFF00796B)
            ),
            icon = "🚚"
        ),
        OnboardingPage(
            title = "Enjoy Your Meal",
            description = "Savor every bite of carefully prepared dishes made with love and quality ingredients",
            gradientColors = listOf(
                Color(0xFF90CAF9),
                Color(0xFF42A5F5),
                Color(0xFF1976D2)
            ),
            icon = "🍕"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = {
                    markOnboardingCompleted(context)
                    onNavigateToLogin()
                }
            ) {
                Text(
                    text = "Skip",
                    color = Color(0xFF8E8E8E),
                    fontSize = 14.sp
                )
            }
        }

        // Pager content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPageContent(onboardingPages[page])
        }

        // Page indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(onboardingPages.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(
                            width = if (isSelected) 24.dp else 8.dp,
                            height = 8.dp
                        )
                        .clip(CircleShape)
                        .background(
                            if (isSelected) Color(0xFFFF6B35)
                            else Color(0xFFE0E0E0)
                        )
                )
                if (index < onboardingPages.size - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (pagerState.currentPage == onboardingPages.size - 1) {
                Arrangement.Center
            } else {
                Arrangement.SpaceBetween
            }
        ) {
            if (pagerState.currentPage < onboardingPages.size - 1) {
                // Previous button (if not on first page)
                if (pagerState.currentPage > 0) {
                    OutlinedButton(
                        onClick = {
                            // Go to previous page
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFFF6B35)
                        )
                    ) {
                        Text(
                            text = "Previous",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                }

                // Next button
                Button(
                    onClick = {
                        // Go to next page
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6B35)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Next",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            } else {
                Button(
                    onClick = {
                        markOnboardingCompleted(context)
                        onNavigateToLogin()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6B35)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Get Started",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Large colorful background with icon
        Box(
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    brush = Brush.radialGradient(
                        colors = page.gradientColors,
                        radius = 220f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Decorative stars/sparkles
            StarDecorations()

            // Main icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = page.icon,
                    fontSize = 48.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Title
        Text(
            text = page.title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C2C2C),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = page.description,
            fontSize = 14.sp,
            color = Color(0xFF8E8E8E),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun StarDecorations() {
    // Top left star
    Box(
        modifier = Modifier
            .offset(x = 50.dp, y = 40.dp)
            .size(16.dp)
    ) {
        Text(text = "✨", fontSize = 16.sp)
    }

    // Top right star
    Box(
        modifier = Modifier
            .offset(x = 220.dp, y = 60.dp)
            .size(12.dp)
    ) {
        Text(text = "⭐", fontSize = 12.sp)
    }

    // Bottom left star
    Box(
        modifier = Modifier
            .offset(x = 40.dp, y = 200.dp)
            .size(14.dp)
    ) {
        Text(text = "✨", fontSize = 14.sp)
    }

    // Bottom right star
    Box(
        modifier = Modifier
            .offset(x = 230.dp, y = 220.dp)
            .size(18.dp)
    ) {
        Text(text = "⭐", fontSize = 18.sp)
    }

    // Center sparkles
    Box(
        modifier = Modifier
            .offset(x = 150.dp, y = 30.dp)
            .size(10.dp)
    ) {
        Text(text = "✨", fontSize = 10.sp)
    }

    Box(
        modifier = Modifier
            .offset(x = 60.dp, y = 150.dp)
            .size(8.dp)
    ) {
        Text(text = "⭐", fontSize = 8.sp)
    }
}

data class OnboardingPage(
    val title: String,
    val description: String,
    val gradientColors: List<Color>,
    val icon: String
)