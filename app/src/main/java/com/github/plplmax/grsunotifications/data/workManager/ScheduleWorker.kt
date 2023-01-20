package com.github.plplmax.grsunotifications.data.workManager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.github.plplmax.grsunotifications.data.ScheduleRepository
import com.github.plplmax.grsunotifications.data.UserRepository
import com.github.plplmax.grsunotifications.notification.NotificationCentre
import com.github.plplmax.grsunotifications.notification.ScheduleNotification
import org.json.JSONObject
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

class ScheduleWorker(
    private val context: Context,
    workerParams: WorkerParameters,
    private val userRepository: UserRepository,
    private val scheduleRepository: ScheduleRepository,
    private val notificationCentre: NotificationCentre
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        if (!notificationCentre.hasNotificationsPermission) {
            userRepository.deleteId()
            WorkManager.getInstance(context).cancelWorkById(this.id)
            return Result.failure()
        }

        val oldHash = scheduleRepository.scheduleHash()

        if (oldHash.isNotEmpty() && isNightNow()) {
            return Result.success()
        }

        val userId = userRepository.id()
        val (startDate, endDate) = scheduleRange()
        val jsonResult = scheduleRepository.onWeek(userId, startDate, endDate)

        if (jsonResult.isFailure) {
            return Result.retry()
        }

        val newHash = hashed(jsonResult.getOrThrow())
        scheduleRepository.saveScheduleHash(newHash)

        if (oldHash.isEmpty()) {
            ScheduleNotification(context, text = "Расписание было синхронизировано").send()
        } else if (oldHash != newHash) {
            ScheduleNotification(context).send()
        }

        return Result.success()
    }

    private fun isNightNow(): Boolean {
        val now = Calendar.getInstance().time
        val start = startNightTime()
        val end = endNightTime()

        return now >= start && now < end
    }

    private fun currentDateWithTimeOf(hours: Int, minutes: Int = 0, seconds: Int = 0): Date {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, minutes)
            set(Calendar.SECOND, seconds)
        }.time
    }

    private fun startNightTime(): Date = currentDateWithTimeOf(hours = 0)

    private fun endNightTime(): Date = currentDateWithTimeOf(hours = 6)

    private fun scheduleRange(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val dateFormat = SimpleDateFormat("dd.MM.yyyy")

        val startDate = dateFormat.format(calendar.time)
        calendar.add(Calendar.DATE, 9)
        val endDate = dateFormat.format(calendar.time)

        return Pair(startDate, endDate)
    }

    private fun hashed(json: JSONObject): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(json.toString().encodeToByteArray())
        return digest.digest().joinToString { String.format("%02x", it) }
    }
}
