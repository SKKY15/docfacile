package com.example.startup_app.viewmodels

sealed class NotificationDisplayItem {
    data class Header(val title: String) : NotificationDisplayItem()
    data class NotificationItem(
        val type: String,
        val title: String,
        val message: String,
        val time: String,
        val timestamp: Long = System.currentTimeMillis(),
        var isRead : Boolean = false,
    ) : NotificationDisplayItem()
}

