package com.github.plplmax.notifications.computed

import com.github.plplmax.notifications.data.schedule.enums.ModificationType
import com.github.plplmax.notifications.data.schedule.models.Day

class ComputedDaysDiffOf(
    private val old: List<Day>,
    private val new: List<Day>
) : ComputedDaysDiff {
    override fun value(): List<Day> {
        val daysDiffNewToOld = new.map { newDay ->
            old.find { it.date == newDay.date }
                ?.let { ComputedDayDiffOf(old = it, new = newDay).value() }
                ?: kotlin.run {
                    val addedLessons = newDay.lessons.map {
                        it.copy(modificationType = ModificationType.Added)
                    }
                    newDay.copy(lessons = addedLessons)
                }
        }

        val daysDiffOldToNew = old.mapNotNull { oldDay ->
            new.find { it.date == oldDay.date } ?: return@mapNotNull kotlin.run {
                val deletedLessons = oldDay.lessons.map {
                    it.copy(modificationType = ModificationType.Deleted)
                }
                oldDay.copy(lessons = deletedLessons)
            }
            null
        }

        return daysDiffOldToNew + daysDiffNewToOld.filter { it.lessons.isNotEmpty() }
    }
}
