package com.app.tastybuds.util.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tastybuds.R
import com.app.tastybuds.ui.theme.dividerColor
import com.app.tastybuds.ui.theme.topAppBarBackgroundColor
import com.app.tastybuds.ui.theme.topAppBarContentColor

@Preview
@Composable
fun AppTopBar(
    title: String = "Rating",
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Column(modifier = modifier.background(topAppBarBackgroundColor())) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = topAppBarContentColor()
            )

            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_arrow),
                    contentDescription = stringResource(id = R.string.back),
                    tint = topAppBarContentColor()
                )
            }

            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                content = actions
            )
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = dividerColor()
        )
    }
}