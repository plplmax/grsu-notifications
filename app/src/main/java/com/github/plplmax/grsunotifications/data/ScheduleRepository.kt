package com.github.plplmax.grsunotifications.data

import org.json.JSONObject

interface ScheduleRepository {
    suspend fun onWeek(userId: Int, startDate: String, endDate: String): Result<JSONObject>
}
