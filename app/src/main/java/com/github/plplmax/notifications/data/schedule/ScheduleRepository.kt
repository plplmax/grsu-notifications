package com.github.plplmax.notifications.data.schedule

import org.json.JSONObject

interface ScheduleRepository {
    suspend fun onWeek(userId: Int, startDate: String, endDate: String): Result<JSONObject>
    fun saveScheduleHash(hash: String)
    suspend fun scheduleHash(): String
    fun deleteScheduleHash()
}
