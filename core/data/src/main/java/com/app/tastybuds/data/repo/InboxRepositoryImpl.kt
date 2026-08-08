package com.app.tastybuds.data.repo

import com.app.tastybuds.data.local.NotificationDao
import com.app.tastybuds.data.model.mapper.toDomain
import com.app.tastybuds.data.model.mapper.toEntity
import com.app.tastybuds.domain.model.NotificationItem
import com.app.tastybuds.domain.repository.InboxRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room-backed inbox. FCM pushes and backend syncs both funnel into [saveIncoming]; the UI observes
 * [observeNotifications]/[unreadCount] as reactive Flows straight off the database.
 */
@Singleton
class InboxRepositoryImpl @Inject constructor(
    private val dao: NotificationDao
) : InboxRepository {

    override fun observeNotifications(): Flow<List<NotificationItem>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun unreadCount(): Flow<Int> = dao.unreadCount()

    override suspend fun saveIncoming(item: NotificationItem) = dao.upsert(item.toEntity())

    override suspend fun markAsRead(id: String) = dao.markRead(id)

    override suspend fun markAllAsRead() = dao.markAllRead()

    override suspend fun clearAll() = dao.clear()
}
