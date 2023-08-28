package com.github.plplmax.notifications.computed

import com.github.plplmax.notifications.data.schedule.enums.ModificationType
import com.github.plplmax.notifications.data.schedule.models.Lesson

class ComputedLessonsDiffOf(
    private val old: List<Lesson>,
    private val new: List<Lesson>
) : ComputedLessonsDiff {
    override fun value(): List<Lesson> {
        val modifiedLessons = new.flatMap { newLesson ->
            old.find { it.timeStart == newLesson.timeStart }
                ?.let { ComputedLessonDiffOf(it, newLesson).value() }
                ?: listOf(newLesson.copy(modificationType = ModificationType.Added))
        }

        val deletedLessons = old.mapNotNull { oldLesson ->
            new.find { it.timeStart == oldLesson.timeStart }
                ?: return@mapNotNull oldLesson.copy(modificationType = ModificationType.Deleted)
            null
        }

        return (deletedLessons + modifiedLessons).sortedBy { it.timeStart }
    }
}
