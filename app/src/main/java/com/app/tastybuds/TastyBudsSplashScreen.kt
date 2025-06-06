package com.app.tastybuds

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tastybuds.ui.theme.PrimaryColor
import com.app.tastybuds.ui.theme.SetSystemBarColor
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay

// Color palette
object SplashColors {
    val BrandOrange = Color(0xFFFF7700)
    val FreshGreen = Color(0xFF4CAF50)
    val TomatoRed = Color(0xFFE53935)
    val GoldenYellow = Color(0xFFFFC107)
    val Cream = Color(0xFFFFF8E1)
    val White = Color(0xFFFFFFFF)
}

@Composable
fun TastyBudsSplashScreen(
    onSplashComplete: () -> Unit = {}
) {
    val systemUiController = rememberSystemUiController()
    SetSystemBarColor(PrimaryColor)

    DisposableEffect(Unit) {
        onDispose {
            systemUiController.isStatusBarVisible = true
            systemUiController.isNavigationBarVisible = true
        }
    }
    val infiniteTransition = rememberInfiniteTransition(label = "splash_animations")

    val pizzaOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pizza_float"
    )

    val burgerSlide by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "burger_slide"
    )

    val truckOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -30f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "truck_move"
    )

    val bowlScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bowl_bounce"
    )

    val steamOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseOut),
            repeatMode = RepeatMode.Restart
        ),
        label = "steam_rise"
    )

    val logoFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_float"
    )

    val dotScale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )

    val dotScale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, delayMillis = 200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )

    val dotScale3 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, delayMillis = 400, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    val patternOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pattern_move"
    )

    LaunchedEffect(Unit) {
        delay(3000)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashColors.BrandOrange)
    ) {
        // Background pattern
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = patternOffset.dp, y = patternOffset.dp)
        ) {
            drawBackgroundPattern()
        }

        BoxWithConstraints {
            Canvas(
                modifier = Modifier
                    .offset(
                        x = maxWidth * 0.8f,
                        y = (80 + pizzaOffset).dp
                    )
                    .size(80.dp)
            ) {
                drawPizzaSlice()
            }
        }

        Column(
            modifier = Modifier
                .offset(
                    x = (-20 + burgerSlide).dp,
                    y = 150.dp
                )
        ) {
            BurgerLayer(
                width = 80.dp,
                height = 15.dp,
                color = SplashColors.FreshGreen
            )
            Spacer(modifier = Modifier.height(5.dp))
            BurgerLayer(
                width = 90.dp,
                height = 18.dp,
                color = SplashColors.TomatoRed
            )
            Spacer(modifier = Modifier.height(5.dp))
            BurgerLayer(
                width = 85.dp,
                height = 16.dp,
                color = SplashColors.GoldenYellow
            )
        }

        BoxWithConstraints {
            Canvas(
                modifier = Modifier
                    .offset(
                        x = (maxWidth * 0.7f + truckOffset.dp),
                        y = (maxHeight * 0.6f)
                    )
                    .size(width = 80.dp, height = 50.dp)
            ) {
                drawDeliveryTruck()
            }
        }

        Canvas(
            modifier = Modifier
                .offset(x = 30.dp, y = 300.dp)
                .size(60.dp)
                .scale(bowlScale)
        ) {
            drawFoodBowl()
        }

        BoxWithConstraints {
            repeat(3) { index ->
                Canvas(
                    modifier = Modifier
                        .offset(
                            x = maxWidth * 0.6f + (index * 15).dp,
                            y = (250 + steamOffset - index * 5).dp
                        )
                        .size(width = 3.dp, height = 30.dp)
                ) {
                    drawSteamLine()
                }
            }
        }

        BoxWithConstraints {
            Canvas(
                modifier = Modifier
                    .offset(x = 40.dp, y = maxHeight * 0.7f)
                    .size(40.dp)
            ) {
                drawUtensils()
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-120 + logoFloat).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SplashColors.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🍔",
                        fontSize = 40.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "TastyBuds",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = SplashColors.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Delicious food, delivered fast",
                fontSize = 14.sp,
                color = SplashColors.Cream,
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-60).dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LoadingDot(scale = dotScale1)
            LoadingDot(scale = dotScale2)
            LoadingDot(scale = dotScale3)
        }
    }
}

@Composable
private fun BurgerLayer(
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
    color: Color
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.7f))
    )
}

@Composable
private fun LoadingDot(scale: Float) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(SplashColors.White.copy(alpha = 0.8f))
    )
}

private fun DrawScope.drawBackgroundPattern() {
    val patternSize = 60.dp.toPx()
    val cols = (size.width / patternSize).toInt() + 1
    val rows = (size.height / patternSize).toInt() + 1

    for (i in 0..cols) {
        for (j in 0..rows) {
            val x = i * patternSize
            val y = j * patternSize

            when ((i + j) % 4) {
                0 -> drawCircle(
                    color = SplashColors.Cream.copy(alpha = 0.1f),
                    radius = 2.dp.toPx(),
                    center = Offset(x, y)
                )

                1 -> drawCircle(
                    color = SplashColors.FreshGreen.copy(alpha = 0.1f),
                    radius = 1.5.dp.toPx(),
                    center = Offset(x, y)
                )

                2 -> drawCircle(
                    color = SplashColors.TomatoRed.copy(alpha = 0.1f),
                    radius = 1.dp.toPx(),
                    center = Offset(x, y)
                )

                3 -> drawCircle(
                    color = SplashColors.GoldenYellow.copy(alpha = 0.1f),
                    radius = 2.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }
    }
}

private fun DrawScope.drawPizzaSlice() {
    val path = Path().apply {
        moveTo(size.width / 2, size.height / 2)
        lineTo(0f, 0f)
        lineTo(size.width / 2, 0f)
        close()
    }

    drawPath(
        path = path,
        color = SplashColors.Cream.copy(alpha = 0.6f)
    )
}

private fun DrawScope.drawDeliveryTruck() {
    drawRoundRect(
        color = SplashColors.Cream.copy(alpha = 0.5f),
        size = Size(size.width * 0.8f, size.height * 0.6f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
    )

    val wheelRadius = 6.dp.toPx()
    drawCircle(
        color = SplashColors.FreshGreen,
        radius = wheelRadius,
        center = Offset(size.width * 0.2f, size.height * 0.8f)
    )
    drawCircle(
        color = SplashColors.FreshGreen,
        radius = wheelRadius,
        center = Offset(size.width * 0.6f, size.height * 0.8f)
    )
}

private fun DrawScope.drawFoodBowl() {
    drawOval(
        color = SplashColors.TomatoRed.copy(alpha = 0.6f),
        size = Size(size.width, size.height * 0.5f),
        topLeft = Offset(0f, size.height * 0.5f)
    )
}

private fun DrawScope.drawSteamLine() {
    drawRoundRect(
        color = SplashColors.Cream.copy(alpha = 0.4f),
        size = Size(size.width, size.height),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.width / 2)
    )
}

private fun DrawScope.drawUtensils() {
    translate(left = -10.dp.toPx(), top = 0f) {
        rotate(degrees = -15f) {
            // Fork handle
            drawRoundRect(
                color = SplashColors.GoldenYellow.copy(alpha = 0.5f),
                size = Size(4.dp.toPx(), 40.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
            )

            repeat(3) { i ->
                drawRoundRect(
                    color = SplashColors.GoldenYellow.copy(alpha = 0.5f),
                    size = Size(1.5.dp.toPx(), 8.dp.toPx()),
                    topLeft = Offset(i * 2.5.dp.toPx(), -8.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.dp.toPx())
                )
            }
        }
    }

    translate(left = 20.dp.toPx(), top = 5.dp.toPx()) {
        rotate(degrees = 20f) {
            drawRoundRect(
                color = SplashColors.GoldenYellow.copy(alpha = 0.5f),
                size = Size(4.dp.toPx(), 35.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
            )

            drawOval(
                color = SplashColors.GoldenYellow.copy(alpha = 0.5f),
                size = Size(8.dp.toPx(), 12.dp.toPx()),
                topLeft = Offset(-2.dp.toPx(), -12.dp.toPx())
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TastyBudsSplashScreenPreview() {
    TastyBudsSplashScreen()
}