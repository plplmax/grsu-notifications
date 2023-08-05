package com.github.plplmax.notifications.window

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField

class ScheduleUpdateWindowOf(private val date: LocalDate) : ScheduleUpdateWindow {
    private val startDate: LocalDate by lazy {
        date.with(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.value.toLong())
    }

    private val endDate: LocalDate by lazy { startDate.plusDays(9) }

    override fun startDateAsString(formatter: DateTimeFormatter): String {
        return formatter.format(startDate)
    }

    override fun endDateAsString(formatter: DateTimeFormatter): String {
        return formatter.format(endDate)
    }
}
