package com.app.tastybuds.util.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tastybuds.ui.theme.buttonBackgroundColor
import com.app.tastybuds.ui.theme.primaryColor
import com.app.tastybuds.ui.theme.textDisabledColor

@Composable
fun SeeAllButton(
    text: String = "See all",
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonBackgroundColor(),
            contentColor = primaryColor()
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = primaryColor(),
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = textDisabledColor(),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(28.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonsPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
    ) {
        SeeAllButton(onClick = {})
        PrimaryButton(text = "$32", onClick = {})
        PrimaryButton(text = "$25", onClick = {}, enabled = false)
    }
}