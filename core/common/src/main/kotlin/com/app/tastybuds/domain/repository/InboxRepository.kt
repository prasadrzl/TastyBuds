package com.app.tastybuds.domain.repository

import com.app.tastybuds.domain.model.NotificationItem
import kotlinx.coroutines.flow.Flow

/**
 * Local inbox of notifications, backed by an offline-first store (Room). Incoming FCM pushes are
 * saved here so the Inbox tab shows full history even when the app was closed at delivery time.
 */
interface InboxRepository {
    fun observeNotifications(): Flow<List<NotificationItem>>
    fun unreadCount(): Flow<Int>
    suspend fun saveIncoming(item: NotificationItem)
    suspend fun markAsRead(id: String)
    suspend fun markAllAsRead()
    suspend fun clearAll()
}
