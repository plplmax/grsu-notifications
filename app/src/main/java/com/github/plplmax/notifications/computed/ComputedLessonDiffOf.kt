package com.github.plplmax.notifications.computed

import com.github.plplmax.notifications.data.schedule.enums.ModificationType
import com.github.plplmax.notifications.data.schedule.models.Lesson

class ComputedLessonDiffOf(private val old: Lesson, private val new: Lesson) : ComputedLessonDiff {
    override fun value(): List<Lesson> {
        if (new == old) return listOf()
        if (new.title != old.title) {
            return listOf(
                old.copy(modificationType = ModificationType.Deleted),
                new.copy(modificationType = ModificationType.Added)
            )
        }
        var changedLesson = new.copy(modificationType = ModificationType.Edited)
        if (new.teacher.fullname != old.teacher.fullname) {
            changedLesson = changedLesson.copy(
                teacher = changedLesson.teacher.copy(fullname = "+ ${new.teacher.fullname}")
            )
        }
        if (new.type != old.type) {
            changedLesson = changedLesson.copy(
                type = "+ ${changedLesson.type}"
            )
        }
        if (new.address != old.address) {
            changedLesson = changedLesson.copy(
                address = "+ ${changedLesson.address}"
            )
        }
        if (new.room != old.room) {
            changedLesson = changedLesson.copy(
                room = "+ ${changedLesson.room}"
            )
        }
        if (new.fullAddress != old.fullAddress) {
            changedLesson = changedLesson.copy(
                fullAddress = "+ ${changedLesson.fullAddress}"
            )
        }
        return listOf(changedLesson)
    }
}
