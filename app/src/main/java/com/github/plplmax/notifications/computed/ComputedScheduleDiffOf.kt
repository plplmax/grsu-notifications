package com.github.plplmax.notifications.computed

import com.github.plplmax.notifications.data.schedule.models.Day
import com.github.plplmax.notifications.data.schedule.models.DiffedSchedule
import com.github.plplmax.notifications.data.schedule.models.Schedule

class ComputedScheduleDiffOf(
    private val old: Schedule,
    private val new: Schedule
) : ComputedScheduleDiff {
    override fun value(): DiffedSchedule {
        val daysDiffNewToOld = new.days.map { newDay ->
            val foundMatches =
                old.days.find { it.date == newDay.date } ?: kotlin.run {
                    return@map newDay.copy(lessons = newDay.lessons.map { it.copy(isAdded = true) })
                }

            val addedLessons =
                (newDay.lessons - foundMatches.lessons.toSet()).map { it.copy(isAdded = true) }
            val deletedLessons =
                (foundMatches.lessons - newDay.lessons.toSet()).map { it.copy(isDeleted = true) }
            newDay.copy(
                lessons = (deletedLessons + addedLessons).sortedBy { it.timeStart })
        }

        val daysDiffOldToNew = mutableListOf<Day>()

        old.days.forEach { oldDay ->
            new.days.find { it.date == oldDay.date } ?: kotlin.run {
                daysDiffOldToNew.add(
                    oldDay.copy(lessons = oldDay.lessons.map { it.copy(isDeleted = true) })
                )
            }
        }

        // @todo sort days for date (try to swap addition arguments)
        return DiffedSchedule(
            days = daysDiffNewToOld.filter { it.lessons.isNotEmpty() } + daysDiffOldToNew
        )
    }
}
