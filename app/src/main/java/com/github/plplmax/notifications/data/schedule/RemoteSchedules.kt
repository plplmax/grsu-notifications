package com.github.plplmax.notifications.data.schedule

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
import java.time.LocalDate

class RemoteSchedules(
    private val client: OkHttpClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : Schedules {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun onWeek(userId: Int, startDate: String, endDate: String): Result<Schedule> {
        return withContext(dispatcher) {
            kotlin.runCatching {
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
    }

    override suspend fun save(schedule: Schedule) {
        // do nothing
    }

    override suspend fun schedule(): List<Schedule> = listOf()

    override suspend fun deleteSchedule() {
        // do nothing
    }

    override suspend fun lastUpdate(): LocalDate {
        error("lastUpdate must not be invoked")
    }

    override suspend fun saveLastUpdate(date: LocalDate) {
        // do nothing
    }

    override suspend fun deleteLastUpdate() {
        // do nothing
    }
}
