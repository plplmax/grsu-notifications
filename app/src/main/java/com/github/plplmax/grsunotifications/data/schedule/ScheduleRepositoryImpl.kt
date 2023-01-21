package com.github.plplmax.grsunotifications.data.schedule

import com.github.plplmax.grsunotifications.data.result.NetworkResultImpl
import com.github.plplmax.grsunotifications.data.schedule.local.LocalScheduleDataSource
import com.github.plplmax.grsunotifications.data.schedule.remote.RemoteScheduleDataSource
import org.json.JSONObject

class ScheduleRepositoryImpl(
    private val remote: RemoteScheduleDataSource,
    private val local: LocalScheduleDataSource
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
