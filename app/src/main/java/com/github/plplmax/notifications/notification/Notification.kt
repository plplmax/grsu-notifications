package com.github.plplmax.notifications.notification

import com.github.plplmax.notifications.channel.NotificationChannel

interface Notification {
    fun send(channel: NotificationChannel)
}
