package com.github.plplmax.grsunotifications.data.schedule.remote

import org.json.JSONObject

interface RemoteScheduleDataSource {
    suspend fun onWeek(userId: Int, startDate: String, endDate: String): JSONObject
}
