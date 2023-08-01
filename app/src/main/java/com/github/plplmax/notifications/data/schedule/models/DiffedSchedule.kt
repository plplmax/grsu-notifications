package com.github.plplmax.notifications.data.schedule.models

import io.realm.RealmList
import kotlinx.serialization.Serializable

@Serializable
data class DiffedSchedule(val days: List<Day> = listOf())

fun DiffedSchedule.toRealm(): DiffedScheduleRealm {
    return DiffedScheduleRealm().apply {
        days = this@toRealm.days.map(Day::toRealm).toTypedArray().let { RealmList(*it) }
    }
}