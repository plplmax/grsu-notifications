package com.github.plplmax.grsunotifications.data.schedule.remote

import com.github.plplmax.grsunotifications.data.Constants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class ScheduleRemoteDataSourceImpl(
    private val client: OkHttpClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ScheduleRemoteDataSource {
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
