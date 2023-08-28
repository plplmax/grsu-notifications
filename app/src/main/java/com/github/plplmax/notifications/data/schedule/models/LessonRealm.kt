package com.github.plplmax.notifications.data.schedule.models

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass(embedded = true)
open class LessonRealm : RealmObject() {
    var timeStart: String = ""
    var timeEnd: String = ""
    var teacher: TeacherRealm? = TeacherRealm()
    var type: String = ""
    var title: String = ""
    var address: String = ""
    var room: String = ""
    var fullAddress: String = ""
}

fun LessonRealm.toData(): Lesson {
    return Lesson(
        timeStart = timeStart,
        timeEnd = timeEnd,
        teacher = teacher!!.toData(),
        type = type,
        title = title,
        address = address,
        room = room,
        fullAddress = fullAddress
    )
}
