package com.app.tastybuds.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.domain.ChatUseCase
import com.app.tastybuds.domain.model.ChatEvent
import com.app.tastybuds.domain.model.ChatMessage
import com.app.tastybuds.domain.model.SenderType
import com.app.tastybuds.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatUseCase: ChatUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var orderId: String = ""
    private var started = false

    /** Idempotent: safe to call on every recomposition/LaunchedEffect. */
    fun connect(orderId: String) {
        if (started) return
        started = true
        this.orderId = orderId

        viewModelScope.launch {
            val userId = authRepository.getUserId().first().orEmpty()
            _uiState.update { it.copy(currentUserId = userId) }

            chatUseCase.connect(orderId, userId).collect { event ->
                when (event) {
                    is ChatEvent.ConnectionChanged ->
                        _uiState.update { it.copy(connectionState = event.state) }

                    is ChatEvent.MessageReceived ->
                        _uiState.update { it.copy(messages = it.messages + event.message) }
                }
            }
        }
    }

    fun onInputChange(text: String) {
        _uiState.update { it.copy(input = text) }
    }

    fun sendMessage() {
        val text = _uiState.value.input.trim()
        if (text.isEmpty()) return

        val sent = chatUseCase.sendMessage(text)
        if (sent) {
            // Optimistically show the outgoing message; the backend broadcasts only to the driver.
            val optimistic = ChatMessage(
                id = UUID.randomUUID().toString(),
                orderId = orderId,
                senderId = _uiState.value.currentUserId,
                senderType = SenderType.CUSTOMER,
                content = text,
                timestamp = System.currentTimeMillis()
            )
            _uiState.update { it.copy(messages = it.messages + optimistic, input = "") }
        }
    }

    override fun onCleared() {
        chatUseCase.disconnect()
        super.onCleared()
    }
}
