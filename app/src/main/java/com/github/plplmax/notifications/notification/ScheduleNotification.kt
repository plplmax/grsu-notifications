package com.github.plplmax.notifications.notification

import androidx.core.app.NotificationCompat
import com.github.plplmax.notifications.channel.NotificationChannel

class ScheduleNotification(
    private val title: String,
    private val text: String = "",
    private val type: NotificationType = NotificationType.SUCCESSFUL,
) : Notification {
    override fun send(channel: NotificationChannel) {
        channel.builder
            .setContentTitle(this.title)
            .setContentText(this.text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(this.text))
            .build()
            .also { channel.send(type.id, it) }
    }
}
