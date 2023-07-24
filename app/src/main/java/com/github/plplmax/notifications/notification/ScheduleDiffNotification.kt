package com.github.plplmax.notifications.notification

import org.bson.types.ObjectId
import java.time.ZonedDateTime
import java.util.Date

data class ScheduleDiffNotification(
    val id: String = "",
    val read: Boolean = false,
    val created: ZonedDateTime = ZonedDateTime.now()
)

fun ScheduleDiffNotification.toRealm(): ScheduleDiffNotificationRealm {
    return ScheduleDiffNotificationRealm().apply {
        id = if (this@toRealm.id.isEmpty()) ObjectId() else ObjectId(this@toRealm.id)
        read = this@toRealm.read
        created = Date.from(this@toRealm.created.toInstant())
    }
}
