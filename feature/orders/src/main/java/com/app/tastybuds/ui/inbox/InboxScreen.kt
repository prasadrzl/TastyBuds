package com.app.tastybuds.ui.inbox

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.tastybuds.domain.model.NotificationItem
import com.app.tastybuds.ui.theme.backgroundColor
import com.app.tastybuds.ui.theme.onSurfaceColor
import com.app.tastybuds.ui.theme.onSurfaceVariantColor
import com.app.tastybuds.ui.theme.primaryColor
import com.app.tastybuds.ui.theme.surfaceColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InboxScreen(viewModel: InboxViewModel = hiltViewModel()) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(backgroundColor())) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Inbox", color = onSurfaceColor(), fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            if (notifications.any { !it.read }) {
                TextButton(onClick = viewModel::markAllRead) { Text("Mark all read") }
            }
        }

        if (notifications.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No notifications yet", color = onSurfaceVariantColor())
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(notifications, key = { it.id }) { item ->
                    NotificationRow(item, onClick = { viewModel.markRead(item.id) })
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(item: NotificationItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(surfaceColor())
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(8.dp).clip(CircleShape)
                .background(if (item.read) surfaceColor() else primaryColor())
        )
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                item.title,
                color = onSurfaceColor(),
                fontSize = 16.sp,
                fontWeight = if (item.read) FontWeight.Normal else FontWeight.Bold
            )
            if (item.body.isNotBlank()) {
                Text(item.body, color = onSurfaceVariantColor(), fontSize = 14.sp)
            }
        }
        Spacer(Modifier.size(8.dp))
        Text(formatTime(item.timestamp), color = onSurfaceVariantColor(), fontSize = 11.sp)
    }
}

private fun formatTime(timestamp: Long): String =
    SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(timestamp))
