package com.github.plplmax.notifications.data.schedule.remote

import com.github.plplmax.notifications.data.Constants
import com.github.plplmax.notifications.data.schedule.models.Schedule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class ScheduleRemoteDataSourceImpl(
    private val client: OkHttpClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ScheduleRemoteDataSource {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun onWeek(userId: Int, startDate: String, endDate: String): Schedule =
        withContext(dispatcher) {
            val request = Request.Builder()
                .url("${Constants.BASE_URL}/getGroupSchedule?studentId=$userId&dateStart=$startDate&dateEnd=$endDate")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val days = JSONObject(response.body!!.string())
                // @todo extract to the DI
                json.decodeFromString(days.toString())
            }
        }
}
