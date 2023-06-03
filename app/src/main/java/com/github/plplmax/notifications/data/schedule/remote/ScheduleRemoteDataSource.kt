package com.github.plplmax.notifications.data.schedule.remote

import com.github.plplmax.notifications.data.schedule.models.Schedule

interface ScheduleRemoteDataSource {
    suspend fun onWeek(userId: Int, startDate: String, endDate: String): Schedule
}
