package com.github.plplmax.grsunotifications.data.impl

import com.github.plplmax.grsunotifications.data.Constants
import com.github.plplmax.grsunotifications.data.RemoteScheduleDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class RemoteScheduleDataSourceImpl(
    private val client: OkHttpClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : RemoteScheduleDataSource {
    override suspend fun onWeek(userId: Int, startDate: String, endDate: String): JSONObject =
        withContext(dispatcher) {
            val request = Request.Builder()
                .url("${Constants.BASE_URL}/getGroupSchedule?studentId=$userId&dateStart=$startDate&dateEnd=$endDate")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                JSONObject(response.body!!.string())
            }
        }
}
