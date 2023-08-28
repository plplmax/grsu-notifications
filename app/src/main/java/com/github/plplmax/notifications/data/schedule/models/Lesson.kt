package com.github.plplmax.notifications.data.schedule.models

import com.github.plplmax.notifications.data.schedule.enums.ModificationType
import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    val timeStart: String = "",
    val timeEnd: String = "",
    val teacher: Teacher = Teacher(),
    val type: String = "",
    val title: String = "",
    val address: String = "",
    val room: String = "",
    val fullAddress: String = if (room.isEmpty()) {
        address
    } else {
        "$address, $room"
    },
    val modificationType: ModificationType = ModificationType.None
)

fun Lesson.toRealm(): LessonRealm {
    return LessonRealm().apply {
        timeStart = this@toRealm.timeStart
        timeEnd = this@toRealm.timeEnd
        teacher = this@toRealm.teacher.toRealm()
        type = this@toRealm.type
        title = this@toRealm.title
        address = this@toRealm.address
        room = this@toRealm.room
        fullAddress = this@toRealm.fullAddress
        modificationType = this@toRealm.modificationType
    }
}
