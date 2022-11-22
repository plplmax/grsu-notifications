package com.github.plplmax.grsunotifications.data

import org.json.JSONObject

interface RemoteScheduleDataSource {
    suspend fun onWeek(userId: Int, startDate: String, endDate: String): JSONObject
}
