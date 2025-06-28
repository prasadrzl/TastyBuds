package com.app.tastybuds.util.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.app.tastybuds.R
import com.app.tastybuds.ui.theme.ComponentSizes
import com.app.tastybuds.ui.theme.Spacing
import com.app.tastybuds.ui.theme.backgroundColor
import com.app.tastybuds.ui.theme.buttonText
import com.app.tastybuds.ui.theme.emptyStateDescription
import com.app.tastybuds.ui.theme.emptyStateTitle
import com.app.tastybuds.ui.theme.errorColor
import com.app.tastybuds.ui.theme.onBackgroundColor
import com.app.tastybuds.ui.theme.textSecondaryColor

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Filled.Refresh,
    title: String = stringResource(R.string.something_went_wrong),
    subtitle: String = stringResource(R.string.please_check_your_connection_and_try_again),
    buttonText: String? = stringResource(R.string.retry),
    onRetryClick: (() -> Unit)? = null,
    backgroundColor: Color = backgroundColor(),
    iconTint: Color = errorColor()
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(Spacing.xl)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Error Icon",
                modifier = Modifier.size(ComponentSizes.iconLarge),
                tint = iconTint
            )

            Spacer(modifier = Modifier.height(Spacing.large))

            Text(
                text = title,
                style = emptyStateTitle(),
                color = onBackgroundColor(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.small))

            Text(
                text = subtitle,
                style = emptyStateDescription(),
                color = textSecondaryColor(),
                textAlign = TextAlign.Center
            )

            if (buttonText != null && onRetryClick != null) {
                Spacer(modifier = Modifier.height(Spacing.xl))

                Button(
                    onClick = onRetryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ComponentSizes.buttonHeight),
                    shape = RoundedCornerShape(ComponentSizes.cornerRadius)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = stringResource(R.string.retry),
                        modifier = Modifier.size(ComponentSizes.iconSmall)
                    )
                    Spacer(modifier = Modifier.width(Spacing.small))
                    Text(
                        text = buttonText,
                        style = buttonText()
                    )
                }
            }
        }
    }
}

object ErrorScreenVariants {
    @Composable
    fun NetworkError(
        modifier: Modifier = Modifier,
        onRetryClick: (() -> Unit)? = null
    ) {
        ErrorScreen(
            modifier = modifier,
            icon = Icons.Filled.Refresh,
            title = stringResource(R.string.no_internet_connection),
            subtitle = stringResource(R.string.please_check_your_internet_connection_and_try_again),
            buttonText = stringResource(R.string.retry),
            onRetryClick = onRetryClick
        )
    }

    @Composable
    fun ServerError(
        modifier: Modifier = Modifier,
        onRetryClick: (() -> Unit)? = null
    ) {
        ErrorScreen(
            modifier = modifier,
            icon = Icons.Filled.Refresh,
            title = stringResource(R.string.server_error),
            subtitle = stringResource(R.string.we_re_experiencing_some_technical_difficulties_please_try_again_later),
            buttonText = stringResource(R.string.retry),
            onRetryClick = onRetryClick
        )
    }

    @Composable
    fun NotFoundError(
        modifier: Modifier = Modifier,
        onRetryClick: (() -> Unit)? = null
    ) {
        ErrorScreen(
            modifier = modifier,
            icon = Icons.Filled.Refresh,
            title = stringResource(R.string.content_not_found),
            subtitle = stringResource(R.string.the_content_you_re_looking_for_does_not_exist_or_has_been_moved),
            buttonText = stringResource(R.string.go_back),
            onRetryClick = onRetryClick
        )
    }
}