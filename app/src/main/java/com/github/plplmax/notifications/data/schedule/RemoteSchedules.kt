package com.github.plplmax.notifications.data.schedule

import com.github.plplmax.notifications.data.schedule.models.Schedule
import com.github.plplmax.notifications.data.toStringForLogging
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.IOException
import java.time.LocalDate

class RemoteSchedules(
    private val client: HttpClient,
    private val json: Json,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : Schedules {
    override suspend fun onWeek(userId: Int, startDate: String, endDate: String): Result<Schedule> {
        return withContext(dispatcher) {
            try {
                val response = client.get("getGroupSchedule") {
                    parameter("studentId", userId)
                    parameter("dateStart", startDate)
                    parameter("dateEnd", endDate)
                }
                if (!response.status.isSuccess()) throw IOException("Unexpected code ${response.toStringForLogging()}")
                val body = response.bodyAsText()
                val schedule = json.decodeFromString<Schedule>(body)
                Result.success(schedule)
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                Result.failure(e)
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
