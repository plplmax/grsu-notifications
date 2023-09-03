package com.github.plplmax.notifications.window

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ScheduleUpdateWindowOf(date: LocalDate) : ScheduleUpdateWindow {
    private val startDate: LocalDate = date
    private val endDate: LocalDate by lazy { startDate.plusDays(6) }

    override fun startDateAsString(formatter: DateTimeFormatter): String {
        return formatter.format(startDate)
    }

    override fun endDateAsString(formatter: DateTimeFormatter): String {
        return formatter.format(endDate)
    }
}
