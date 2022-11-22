package com.github.plplmax.grsunotifications.data.impl

import com.github.plplmax.grsunotifications.data.Errors
import com.github.plplmax.grsunotifications.data.RemoteScheduleDataSource
import com.github.plplmax.grsunotifications.data.ScheduleRepository
import org.json.JSONObject
import java.net.UnknownHostException

class ScheduleRepositoryImpl(private val remote: RemoteScheduleDataSource) : ScheduleRepository {
    override suspend fun onWeek(userId: Int, startDate: String, endDate: String): Result<JSONObject> {
        return kotlin.runCatching {
            try {
                remote.onWeek(userId, startDate, endDate)
            } catch (e: Exception) {
                when (e) {
                    is UnknownHostException -> error(Errors.CHECK_INTERNET_CONNECTION)
                    else -> error(Errors.GENERIC_ERROR)
                }
            }
        }
    }
}
