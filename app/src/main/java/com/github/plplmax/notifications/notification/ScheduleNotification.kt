package com.github.plplmax.notifications.notification

import androidx.core.app.NotificationCompat

class ScheduleNotification(
    private val title: String = "Уведомления ГрГУ",
    private val text: String = "Расписание было обновлено",
) : Notification {
    override fun send(channel: NotificationChannel) {
        channel.builder
            .setContentTitle(this.title)
            .setContentText(this.text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(this.text))
            .build()
            .also(channel::send)
    }
}
