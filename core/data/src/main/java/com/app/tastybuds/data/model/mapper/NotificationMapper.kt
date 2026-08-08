package com.app.tastybuds.data.model.mapper

import com.app.tastybuds.data.local.NotificationEntity
import com.app.tastybuds.domain.model.NotificationItem

fun NotificationEntity.toDomain(): NotificationItem = NotificationItem(
    id = id, title = title, body = body, type = type,
    timestamp = timestamp, read = read, deepLink = deepLink
)

fun NotificationItem.toEntity(): NotificationEntity = NotificationEntity(
    id = id, title = title, body = body, type = type,
    timestamp = timestamp, read = read, deepLink = deepLink
)
