package com.github.plplmax.notifications.data.schedule.models

import io.realm.RealmList
import kotlinx.serialization.Serializable

@Serializable
data class Day(
    val date: String,
    val lessons: List<Lesson>
)

fun Day.toRealm(): DayRealm {
    val lessonsRealm = this@toRealm.lessons.map(Lesson::toRealm).toTypedArray()
    return DayRealm().apply {
        date = this@toRealm.date
        lessons = RealmList(*lessonsRealm)
    }
}
