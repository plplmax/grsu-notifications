package com.github.plplmax.notifications.window

import com.github.plplmax.notifications.data.schedule.models.Schedule
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface ScheduleUpdateWindow {
    val durationInDays: Long
    fun startDateAsString(formatter: DateTimeFormatter): String
    fun endDateAsString(formatter: DateTimeFormatter): String
    fun normalizedOldSchedule(old: Schedule, new: Schedule, lastUpdate: LocalDate): Schedule
}
