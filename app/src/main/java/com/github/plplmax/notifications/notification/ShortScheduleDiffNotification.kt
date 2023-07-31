package com.github.plplmax.notifications.notification

import java.time.ZonedDateTime

data class ShortScheduleDiffNotification(
    val id: String = "",
    val read: Boolean = false,
    val created: ZonedDateTime = ZonedDateTime.now()
)
