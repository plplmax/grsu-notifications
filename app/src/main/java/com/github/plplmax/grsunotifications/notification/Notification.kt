package com.github.plplmax.grsunotifications.notification

interface Notification {
    fun send(channel: NotificationChannel)
}
