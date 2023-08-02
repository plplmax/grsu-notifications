package com.github.plplmax.notifications.data.notification.models

import java.time.ZonedDateTime

data class ShortScheduleDiffNotification(
    val id: String = "",
    val read: Boolean = false,
    val created: ZonedDateTime = ZonedDateTime.now()
)
