package com.app.tastybuds.ui.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.domain.InboxUseCase
import com.app.tastybuds.domain.model.NotificationItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val inboxUseCase: InboxUseCase
) : ViewModel() {

    val notifications: StateFlow<List<NotificationItem>> =
        inboxUseCase.notifications()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun markRead(id: String) {
        viewModelScope.launch { inboxUseCase.markRead(id) }
    }

    fun markAllRead() {
        viewModelScope.launch { inboxUseCase.markAllRead() }
    }
}
