package com.github.plplmax.notifications.data.schedule

import com.github.plplmax.notifications.data.schedule.models.Schedule

interface ScheduleRepository {
    suspend fun onWeek(userId: Int, startDate: String, endDate: String): Result<Schedule>
    suspend fun save(schedule: Schedule)
    suspend fun schedule(): List<Schedule>
    suspend fun deleteSchedule()
}
