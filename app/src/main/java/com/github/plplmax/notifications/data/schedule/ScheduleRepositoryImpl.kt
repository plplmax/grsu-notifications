package com.github.plplmax.notifications.data.schedule

import com.github.plplmax.notifications.data.result.NetworkResultImpl
import com.github.plplmax.notifications.data.schedule.local.ScheduleLocalDataSource
import com.github.plplmax.notifications.data.schedule.models.Schedule
import com.github.plplmax.notifications.data.schedule.models.ScheduleRealm
import com.github.plplmax.notifications.data.schedule.models.toData
import com.github.plplmax.notifications.data.schedule.models.toRealm
import com.github.plplmax.notifications.data.schedule.remote.ScheduleRemoteDataSource

class ScheduleRepositoryImpl(
    private val remote: ScheduleRemoteDataSource,
    private val local: ScheduleLocalDataSource
) : ScheduleRepository {
    override suspend fun onWeek(
        userId: Int,
        startDate: String,
        endDate: String
    ): Result<Schedule> {
        return NetworkResultImpl { remote.onWeek(userId, startDate, endDate) }.result()
    }

    override suspend fun save(schedule: Schedule) {
        local.insert(schedule.toRealm())
    }

    override suspend fun deleteSchedule() {
        local.deleteSchedule()
    }

    override suspend fun schedule(): List<Schedule> {
        return local.schedule().map(ScheduleRealm::toData)
    }
}
