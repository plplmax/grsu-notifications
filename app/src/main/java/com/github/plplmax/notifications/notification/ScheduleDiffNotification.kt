package com.github.plplmax.notifications.notification

import java.util.Date

data class ScheduleDiffNotification(
    val id: String,
    val read: Boolean,
    val created: Date
)
