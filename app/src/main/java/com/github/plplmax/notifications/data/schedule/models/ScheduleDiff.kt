package com.github.plplmax.notifications.data.schedule.models

import io.realm.RealmList
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleDiff(val days: List<Day> = listOf())

fun ScheduleDiff.toRealm(): ScheduleDiffRealm {
    return ScheduleDiffRealm().apply {
        days = this@toRealm.days.map(Day::toRealm).toTypedArray().let { RealmList(*it) }
    }
}