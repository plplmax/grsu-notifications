package com.github.plplmax.notifications.data.schedule.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass(embedded = true)
open class ScheduleDiffRealm : RealmObject() {
    var days: RealmList<DayRealm> = RealmList()
}

fun ScheduleDiffRealm.toData(): ScheduleDiff {
    return ScheduleDiff(days = days.map(DayRealm::toData))
}
