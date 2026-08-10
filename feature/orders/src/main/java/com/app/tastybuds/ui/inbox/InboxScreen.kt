package com.app.tastybuds.ui.inbox

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tastybuds.feature.orders.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.tastybuds.domain.model.NotificationItem
import com.app.tastybuds.ui.theme.backgroundColor
import com.app.tastybuds.ui.theme.cardBackgroundColor
import com.app.tastybuds.ui.theme.infoContainerColor
import com.app.tastybuds.ui.theme.offerBackgroundColor
import com.app.tastybuds.ui.theme.offerTextColor
import com.app.tastybuds.ui.theme.onInfoContainerColor
import com.app.tastybuds.ui.theme.onSuccessContainerColor
import com.app.tastybuds.ui.theme.onSurfaceColor
import com.app.tastybuds.ui.theme.onSurfaceVariantColor
import com.app.tastybuds.ui.theme.primaryColor
import com.app.tastybuds.ui.theme.successContainerColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InboxScreen(viewModel: InboxViewModel = hiltViewModel()) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unread = notifications.count { !it.read }

    Column(modifier = Modifier.fillMaxSize().background(backgroundColor())) {
        InboxHeader(unread = unread, onMarkAllRead = viewModel::markAllRead)

        if (notifications.isEmpty()) {
            EmptyInbox()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications, key = { it.id }) { item ->
                    NotificationCard(item, onClick = { viewModel.markRead(item.id) })
                }
            }
        }
    }
}

@Composable
private fun InboxHeader(unread: Int, onMarkAllRead: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.inbox_title),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = onSurfaceColor()
            )
            Text(
                text = if (unread > 0) {
                    pluralStringResource(R.plurals.inbox_unread_count, unread, unread)
                } else {
                    stringResource(R.string.inbox_all_caught_up)
                },
                style = MaterialTheme.typography.bodySmall,
                color = onSurfaceVariantColor()
            )
        }
        if (unread > 0) {
            TextButton(onClick = onMarkAllRead) {
                Text(
                    stringResource(R.string.inbox_mark_all_read),
                    color = primaryColor(),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun NotificationCard(item: NotificationItem, onClick: () -> Unit) {
    val style = styleFor(item.type)
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.read) 1.dp else 3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Type icon badge
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(style.container),
                contentAlignment = Alignment.Center
            ) {
                Icon(style.icon, contentDescription = null, tint = style.onContainer, modifier = Modifier.size(22.dp))
            }

            Spacer(Modifier.size(12.dp))

            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (item.read) FontWeight.Medium else FontWeight.Bold
                        ),
                        color = onSurfaceColor(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (!item.read) {
                        Spacer(Modifier.size(6.dp))
                        Box(Modifier.size(8.dp).clip(CircleShape).background(primaryColor()))
                    }
                }

                if (item.body.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = item.body,
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurfaceVariantColor(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TypePill(label = style.label, container = style.container, content = style.onContainer)
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = relativeTime(item.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = onSurfaceVariantColor()
                    )
                }
            }
        }
    }
}

@Composable
private fun TypePill(label: String, container: Color, content: Color) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(container)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(label, color = content, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun EmptyInbox() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(72.dp).clip(CircleShape).background(infoContainerColor()),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Notifications, contentDescription = null,
                tint = onInfoContainerColor(), modifier = Modifier.size(34.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            stringResource(R.string.inbox_empty_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = onSurfaceColor()
        )
        Spacer(Modifier.height(4.dp))
        Text(
            stringResource(R.string.inbox_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = onSurfaceVariantColor()
        )
    }
}

private data class NotificationStyle(
    val icon: ImageVector,
    val label: String,
    val container: Color,
    val onContainer: Color
)

@Composable
private fun styleFor(type: String): NotificationStyle = when (type.uppercase()) {
    "ORDER" -> NotificationStyle(Icons.Filled.ShoppingCart, stringResource(R.string.notif_type_order), infoContainerColor(), onInfoContainerColor())
    "PROMO" -> NotificationStyle(Icons.Filled.Star, stringResource(R.string.notif_type_promo), offerBackgroundColor(), offerTextColor())
    "CHAT" -> NotificationStyle(Icons.Filled.Email, stringResource(R.string.notif_type_chat), successContainerColor(), onSuccessContainerColor())
    else -> NotificationStyle(Icons.Filled.Notifications, stringResource(R.string.notif_type_update), infoContainerColor(), onInfoContainerColor())
}

@Composable
private fun relativeTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60_000 -> stringResource(R.string.time_just_now)
        diff < 3_600_000 -> stringResource(R.string.time_minutes_ago, diff / 60_000)
        diff < 86_400_000 -> stringResource(R.string.time_hours_ago, diff / 3_600_000)
        diff < 604_800_000 -> stringResource(R.string.time_days_ago, diff / 86_400_000)
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
    }
}
