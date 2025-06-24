package com.app.tastybuds.util.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.tastybuds.R
import com.app.tastybuds.ui.theme.onPrimaryColor
import com.app.tastybuds.ui.theme.primaryColor

@Preview
@Composable
fun HomeTopBar(
    onProfileClick: () -> Unit = {},
    onLocationClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(primaryColor())
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = onPrimaryColor()
                )
            ) { onLocationClick() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_user_location),
                contentDescription = stringResource(R.string.location),
                tint = onPrimaryColor(),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.home),
                style = MaterialTheme.typography.titleMedium,
                color = onPrimaryColor()
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_profile_setting),
            contentDescription = stringResource(R.string.profile),
            tint = onPrimaryColor(),
            modifier = Modifier
                .size(24.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = false,
                        radius = 20.dp,
                        color = onPrimaryColor()
                    )
                ) { onProfileClick() }
        )
    }
}