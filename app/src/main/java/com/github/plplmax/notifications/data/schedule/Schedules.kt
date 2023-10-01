package com.github.plplmax.notifications.data.schedule

import com.github.plplmax.notifications.data.schedule.models.Schedule
import java.time.LocalDate

interface Schedules {
    suspend fun onWeek(userId: Int, startDate: String, endDate: String): Result<Schedule>
    suspend fun save(schedule: Schedule)
    suspend fun schedule(): List<Schedule>
    suspend fun deleteSchedule()
    suspend fun lastUpdate(): LocalDate
    suspend fun saveLastUpdate(date: LocalDate)
    suspend fun deleteLastUpdate()
}
