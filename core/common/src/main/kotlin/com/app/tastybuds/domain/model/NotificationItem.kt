package com.app.tastybuds.domain.model

/**
 * A single inbox notification. Domain model — pure Kotlin. The same shape is used whether the
 * notification arrived via FCM (push) or was fetched/synced from the backend.
 */
data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val type: String,        // e.g. "ORDER", "PROMO", "CHAT" — drives icon/routing
    val timestamp: Long,
    val read: Boolean,
    val deepLink: String? = null
)
