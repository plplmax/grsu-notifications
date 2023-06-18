package com.github.plplmax.notifications.notification

import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri

class ScheduleDiffNotification(
    private val id: String,
    private val title: String,
    private val text: String = "",
    private val type: NotificationType = NotificationType.SUCCESSFUL,
) : Notification {
    override fun send(channel: NotificationChannel) {
        val url = "https://github.com/plplmax/grsu-notifications?id=$id"
        val pendingIntent = channel.pendingIntent {
            action = Intent.ACTION_VIEW
            data = url.toUri()
        }
        channel.builder
            .setContentTitle(this.title)
            .setContentText(this.text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(this.text))
            .setContentIntent(pendingIntent)
            .build()
            .also { channel.send(type.id, it) }
    }
}