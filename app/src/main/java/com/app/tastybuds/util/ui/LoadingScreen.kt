package com.app.tastybuds.util.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.app.tastybuds.R
import com.app.tastybuds.ui.theme.ComponentSizes
import com.app.tastybuds.ui.theme.Spacing
import com.app.tastybuds.ui.theme.backgroundColor
import com.app.tastybuds.ui.theme.bodyLarge
import com.app.tastybuds.ui.theme.loadingIndicatorColor
import com.app.tastybuds.ui.theme.onBackgroundColor

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    message: String = "",
    backgroundColor: Color = backgroundColor()
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(ComponentSizes.iconMedium),
                color = loadingIndicatorColor(),
                strokeWidth = ComponentSizes.strokeWidth * 4
            )

            Spacer(modifier = Modifier.height(Spacing.medium))

            if (message.isNotBlank()) {
                Text(
                    text = message,
                    style = bodyLarge(),
                    color = onBackgroundColor(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingScreenPreview() {
    LoadingScreen(message = "Loading your data...")
}