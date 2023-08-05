package com.github.plplmax.notifications.window

import java.time.format.DateTimeFormatter

interface ScheduleUpdateWindow {
    fun startDateAsString(formatter: DateTimeFormatter): String
    fun endDateAsString(formatter: DateTimeFormatter): String
}
