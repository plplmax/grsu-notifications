package com.github.plplmax.grsunotifications.data.impl

import com.github.plplmax.grsunotifications.data.LocalScheduleDataSource
import com.github.plplmax.grsunotifications.data.RemoteScheduleDataSource
import com.github.plplmax.grsunotifications.data.ScheduleRepository
import com.github.plplmax.grsunotifications.data.result.NetworkResultImpl
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
