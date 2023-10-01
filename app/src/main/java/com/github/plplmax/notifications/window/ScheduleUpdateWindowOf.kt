package com.github.plplmax.notifications.window

import com.github.plplmax.notifications.data.schedule.models.Schedule
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ScheduleUpdateWindowOf(date: LocalDate) : ScheduleUpdateWindow {
    private val startDate: LocalDate = date
    private val endDate: LocalDate by lazy { startDate.plusDays(6) }
    override val durationInDays: Long by lazy { startDate.until(endDate, ChronoUnit.DAYS) + 1 }

    override fun startDateAsString(formatter: DateTimeFormatter): String {
        return formatter.format(startDate)
    }

    override fun endDateAsString(formatter: DateTimeFormatter): String {
        return formatter.format(endDate)
    }

    override fun normalizedOldSchedule(
        old: Schedule,
        new: Schedule,
        lastUpdate: LocalDate
    ): Schedule {
        if (lastUpdate in startDate..endDate) return old
        val daysPassed = lastUpdate.until(startDate, ChronoUnit.DAYS)
        if (daysPassed >= durationInDays) return new
        val daysToAdd = ((daysPassed - 1)..0).map { endDate.minusDays(it).toString() }
            .mapNotNull { dateToAdd -> new.days.find { it.date == dateToAdd } }
        val normalizedOldDays = old.days.filter { it.date >= startDate.toString() } + daysToAdd
        return Schedule(days = normalizedOldDays)
    }
}
