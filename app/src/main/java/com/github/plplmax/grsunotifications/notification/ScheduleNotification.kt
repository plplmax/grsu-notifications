package com.github.plplmax.grsunotifications.notification

class ScheduleNotification(
    private val title: String = "Уведомления ГрГУ",
    private val text: String = "Расписание было обновлено",
) : Notification {
    override fun send(channel: NotificationChannel) {
        channel.builder
            .setContentTitle(this.title)
            .setContentText(this.text)
            .build()
            .also(channel::send)
    }
}
