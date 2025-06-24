package com.app.tastybuds.util.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tastybuds.R
import com.app.tastybuds.ui.theme.*

@Preview
@Composable
fun HomeSearchBar(
    value: String = "",
    onValueChange: (String) -> Unit = {},
    onSearchBarClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(primaryColor())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        TextField(
            value = "",
            onValueChange = { },
            placeholder = {
                Text(
                    text = stringResource(R.string.search),
                    color = placeholderTextColor(),
                    fontSize = 12.sp
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = stringResource(R.string.search_icon),
                    tint = onSurfaceVariantColor()
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = surfaceColor(),
                unfocusedContainerColor = surfaceColor(),
                disabledContainerColor = surfaceColor(),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = onSurfaceColor(),
                focusedTextColor = onSurfaceColor(),
                unfocusedTextColor = onSurfaceColor(),
                disabledTextColor = onSurfaceColor(),
                focusedLeadingIconColor = onSurfaceVariantColor(),
                unfocusedLeadingIconColor = onSurfaceVariantColor(),
                disabledLeadingIconColor = onSurfaceVariantColor(),
                focusedPlaceholderColor = placeholderTextColor(),
                unfocusedPlaceholderColor = placeholderTextColor(),
                disabledPlaceholderColor = placeholderTextColor()
            ),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 44.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onSearchBarClick()
                },
            enabled = false,
            readOnly = true,
            singleLine = true
        )
    }
}