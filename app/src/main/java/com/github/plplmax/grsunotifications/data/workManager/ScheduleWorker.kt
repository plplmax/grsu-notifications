package com.github.plplmax.grsunotifications.data.workManager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.plplmax.grsunotifications.data.ScheduleRepository
import com.github.plplmax.grsunotifications.data.UserRepository
import com.github.plplmax.grsunotifications.notification.ScheduleNotification
import org.json.JSONObject
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

class ScheduleWorker(
    private val context: Context,
    workerParams: WorkerParameters,
    private val userRepository: UserRepository,
    private val scheduleRepository: ScheduleRepository
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val userId = userRepository.id()
        val (startDate, endDate) = scheduleRange()
        val jsonResult = scheduleRepository.onWeek(userId, startDate, endDate)

        if (jsonResult.isFailure) {
            return Result.retry()
        }

        val oldHash = scheduleRepository.scheduleHash()
        val newHash = hashed(jsonResult.getOrThrow())
        scheduleRepository.saveScheduleHash(newHash)

        if (oldHash.isEmpty()) {
            ScheduleNotification(context, text = "Расписание было синхронизировано").send()
        } else if (oldHash != newHash) {
            ScheduleNotification(context).send()
        }

        return Result.success()
    }

    private fun scheduleRange(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")

        val startDate = dateFormat.format(calendar.time)
        calendar.add(Calendar.DATE, 6)
        val endDate = dateFormat.format(calendar.time)

        return Pair(startDate, endDate)
    }

    private fun hashed(json: JSONObject): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(json.toString().encodeToByteArray())
        return digest.digest().joinToString { String.format("%02x", it) }
    }
}
