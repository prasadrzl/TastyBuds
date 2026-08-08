package com.app.tastybuds.domain

import com.app.tastybuds.domain.model.NotificationItem
import com.app.tastybuds.domain.repository.InboxRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InboxUseCase @Inject constructor(
    private val repository: InboxRepository
) {
    fun notifications(): Flow<List<NotificationItem>> = repository.observeNotifications()

    fun unreadCount(): Flow<Int> = repository.unreadCount()

    suspend fun save(item: NotificationItem) = repository.saveIncoming(item)

    suspend fun markRead(id: String) = repository.markAsRead(id)

    suspend fun markAllRead() = repository.markAllAsRead()

    suspend fun clear() = repository.clearAll()
}
