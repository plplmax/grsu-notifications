package com.github.plplmax.grsunotifications.data.schedule

import com.github.plplmax.grsunotifications.data.result.NetworkResultImpl
import com.github.plplmax.grsunotifications.data.schedule.local.ScheduleLocalDataSource
import com.github.plplmax.grsunotifications.data.schedule.remote.ScheduleRemoteDataSource
import org.json.JSONObject

class ScheduleRepositoryImpl(
    private val remote: ScheduleRemoteDataSource,
    private val local: ScheduleLocalDataSource
) : ScheduleRepository {
    override suspend fun onWeek(
        userId: Int,
        startDate: String,
        endDate: String
    ): Result<JSONObject> {
        return NetworkResultImpl { remote.onWeek(userId, startDate, endDate) }.result()
    }

    override fun saveScheduleHash(hash: String) {
        local.saveScheduleHash(hash)
    }

    override fun deleteScheduleHash() {
        local.deleteScheduleHash()
    }

    override suspend fun scheduleHash(): String = local.scheduleHash()
}
