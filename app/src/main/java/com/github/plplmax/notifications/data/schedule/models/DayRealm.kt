package com.github.plplmax.notifications.data.schedule.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass(embedded = true)
open class DayRealm : RealmObject() {
    var date: String = ""
    var lessons: RealmList<LessonRealm> = RealmList()
}

fun DayRealm.toData(): Day {
    return Day(date = date, lessons = lessons.map(LessonRealm::toData))
}
