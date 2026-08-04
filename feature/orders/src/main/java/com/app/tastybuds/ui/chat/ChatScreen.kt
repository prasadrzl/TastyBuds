package com.app.tastybuds.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.tastybuds.domain.model.ChatConnectionState
import com.app.tastybuds.domain.model.ChatMessage
import com.app.tastybuds.domain.model.SenderType
import com.app.tastybuds.ui.theme.SuccessColor
import com.app.tastybuds.ui.theme.backgroundColor
import com.app.tastybuds.ui.theme.onPrimaryColor
import com.app.tastybuds.ui.theme.onSurfaceColor
import com.app.tastybuds.ui.theme.onSurfaceVariantColor
import com.app.tastybuds.ui.theme.primaryColor
import com.app.tastybuds.ui.theme.surfaceColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    orderId: String,
    onBackClick: () -> Unit = {},
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(orderId) { viewModel.connect(orderId) }

    Column(modifier = Modifier.fillMaxSize().background(backgroundColor())) {
        ChatHeader(state.connectionState, onBackClick)

        val listState = rememberLazyListState()
        LaunchedEffect(state.messages.size) {
            if (state.messages.isNotEmpty()) listState.animateScrollToItem(state.messages.lastIndex)
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
        ) {
            items(state.messages, key = { it.id }) { message ->
                MessageBubble(message, isMine = message.senderType == SenderType.CUSTOMER)
            }
        }

        MessageInput(
            value = state.input,
            enabled = state.connectionState == ChatConnectionState.CONNECTED,
            onValueChange = viewModel::onInputChange,
            onSend = viewModel::sendMessage
        )
    }
}

@Composable
private fun ChatHeader(connectionState: ChatConnectionState, onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(primaryColor()).padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onPrimaryColor())
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("Delivery chat", color = onPrimaryColor(), fontSize = 18.sp)
            Text(
                text = when (connectionState) {
                    ChatConnectionState.CONNECTING -> "Connecting…"
                    ChatConnectionState.CONNECTED -> "Online"
                    ChatConnectionState.DISCONNECTED -> "Offline"
                    ChatConnectionState.ERROR -> "Connection error"
                },
                color = onPrimaryColor(),
                fontSize = 12.sp
            )
        }
        Box(
            modifier = Modifier.size(10.dp).clip(CircleShape).background(
                if (connectionState == ChatConnectionState.CONNECTED) SuccessColor else Color(0xFFBDBDBD)
            )
        )
        Spacer(Modifier.size(8.dp))
    }
}

@Composable
private fun MessageBubble(message: ChatMessage, isMine: Boolean) {
    val bubbleColor = if (isMine) primaryColor() else surfaceColor()
    val textColor = if (isMine) onPrimaryColor() else onSurfaceColor()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(bubbleColor)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(message.content, color = textColor, fontSize = 15.sp)
            Text(
                text = formatTime(message.timestamp),
                color = if (isMine) onPrimaryColor().copy(alpha = 0.7f) else onSurfaceVariantColor(),
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
private fun MessageInput(
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().background(surfaceColor()).padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            enabled = enabled,
            placeholder = { Text("Message the driver…") },
            maxLines = 4,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { onSend() })
        )
        Spacer(Modifier.size(8.dp))
        IconButton(onClick = onSend, enabled = enabled && value.isNotBlank()) {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = if (enabled && value.isNotBlank()) primaryColor() else onSurfaceVariantColor()
            )
        }
    }
}

private fun formatTime(timestamp: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
