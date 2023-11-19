package com.github.plplmax.notifications.data.schedule

import com.github.plplmax.notifications.data.Errors
import com.github.plplmax.notifications.data.schedule.models.Schedule

class MappedSchedules(private val origin: Schedules) : Schedules by origin {
    override suspend fun onWeek(userId: Int, startDate: String, endDate: String): Result<Schedule> {
        return origin.onWeek(userId, startDate, endDate)
            .fold(
                onSuccess = { Result.success(it) },
                onFailure = { e -> Result.failure(Errors.from(e)) }
            )
    }
}
