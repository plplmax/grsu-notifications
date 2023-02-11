package com.github.plplmax.notifications.notification

interface Notification {
    fun send(channel: NotificationChannel)
}
