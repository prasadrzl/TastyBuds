package com.app.tastybuds.ui.onboarding

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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tastybuds.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit = {},
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    val onboardingPages = listOf(
        OnboardingPage(
            title = stringResource(R.string.onboarding_title_1),
            description = stringResource(R.string.onboarding_desc_1),
            gradientColors = listOf(
                Color(0xFFFFF3E6),
                Color(0xFFFFE6CC),
                Color(0xFFFFD9B3)
            ),
            icon = "🍽️"
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_title_2),
            description = stringResource(R.string.onboarding_desc_2),
            gradientColors = listOf(
                Color(0xFFE6F7F1),
                Color(0xFFCCF2E8),
                Color(0xFFB3EDDE)
            ),
            icon = "🚚"
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_title_3),
            description = stringResource(R.string.onboarding_desc_3),
            gradientColors = listOf(
                Color(0xFFE8F4FD),
                Color(0xFFD1E9FC),
                Color(0xFFBADDFA)
            ),
            icon = "🍕"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = onboardingPages.getOrNull(pagerState.currentPage)?.gradientColors
                        ?: listOf(
                            Color(0xFFFFF3E6),
                            Color(0xFFFFE6CC),
                            Color(0xFFFFD9B3)
                        )
                )
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = {
                    viewModel.markOnboardingCompleted()
                    onNavigateToLogin()
                }
            ) {
                Text(
                    text = stringResource(R.string.skip),
                    color = Color(0xFF666666),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPageContent(onboardingPages[page])
        }

        PageIndicators(
            pagerState = pagerState,
            pageCount = onboardingPages.size
        )

        NavigationButtons(
            currentPage = pagerState.currentPage,
            pageCount = onboardingPages.size,
            onPrevious = {
                scope.launch {
                    val targetPage = pagerState.currentPage - 1
                    pagerState.animateScrollToPage(targetPage)
                }
            },
            onNext = {
                scope.launch {
                    val targetPage = pagerState.currentPage + 1
                    pagerState.animateScrollToPage(targetPage)
                }
            },
            onGetStarted = {
                viewModel.markOnboardingCompleted()
                onNavigateToLogin()
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PageIndicators(
    pagerState: PagerState,
    pageCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pageCount) { index ->
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
                        else Color(0xFF260303)
                    )
            )
            if (index < pageCount - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun NavigationButtons(
    currentPage: Int,
    pageCount: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onGetStarted: () -> Unit
) {
    val isFirstPage = currentPage == 0
    val isLastPage = currentPage == pageCount - 1

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isLastPage) {
            Arrangement.Center
        } else {
            Arrangement.SpaceBetween
        }
    ) {
        when {
            isLastPage -> {
                Button(
                    onClick = {
                        onGetStarted()
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
                        text = stringResource(R.string.get_started),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            isFirstPage -> {
                Button(
                    onClick = {
                        onNext()
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
                        text = stringResource(R.string.next),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            else -> {
                OutlinedButton(
                    onClick = {
                        onPrevious()
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
                        text = stringResource(R.string.previous),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        onNext()
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
                        text = stringResource(R.string.next),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Color.White.copy(alpha = 0.3f)
                ),
            contentAlignment = Alignment.Center
        ) {
            StarDecorations()

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        Color.White.copy(alpha = 0.7f),
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

        Text(
            text = page.title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            fontSize = 14.sp,
            color = Color(0xFF4A4A4A),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun StarDecorations() {
    Box(
        modifier = Modifier
            .offset(x = 50.dp, y = 40.dp)
            .size(16.dp)
    ) {
        Text(text = "✨", fontSize = 16.sp, color = Color(0xFF666666))
    }

    Box(
        modifier = Modifier
            .offset(x = 220.dp, y = 60.dp)
            .size(12.dp)
    ) {
        Text(text = "⭐", fontSize = 12.sp, color = Color(0xFF666666))
    }

    Box(
        modifier = Modifier
            .offset(x = 40.dp, y = 200.dp)
            .size(14.dp)
    ) {
        Text(text = "✨", fontSize = 14.sp, color = Color(0xFF666666))
    }

    Box(
        modifier = Modifier
            .offset(x = 230.dp, y = 220.dp)
            .size(18.dp)
    ) {
        Text(text = "⭐", fontSize = 18.sp, color = Color(0xFF666666))
    }

    Box(
        modifier = Modifier
            .offset(x = 150.dp, y = 30.dp)
            .size(10.dp)
    ) {
        Text(text = "✨", fontSize = 10.sp, color = Color(0xFF666666))
    }

    Box(
        modifier = Modifier
            .offset(x = 60.dp, y = 150.dp)
            .size(8.dp)
    ) {
        Text(text = "⭐", fontSize = 8.sp, color = Color(0xFF666666))
    }
}

data class OnboardingPage(
    val title: String,
    val description: String,
    val gradientColors: List<Color>,
    val icon: String
)