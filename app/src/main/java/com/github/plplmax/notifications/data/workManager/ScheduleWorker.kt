package com.github.plplmax.notifications.data.workManager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.plplmax.notifications.R
import com.github.plplmax.notifications.data.schedule.ScheduleRepository
import com.github.plplmax.notifications.data.schedule.models.Day
import com.github.plplmax.notifications.data.user.UserRepository
import com.github.plplmax.notifications.notification.ScheduleNotification
import com.github.plplmax.notifications.notification.ScheduleNotificationChannel
import com.github.plplmax.notifications.resources.Resources
import org.json.JSONObject
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class ScheduleWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val userRepository: UserRepository,
    private val scheduleRepository: ScheduleRepository,
    private val notificationChannel: ScheduleNotificationChannel,
    private val resources: Resources
) : CoroutineWorker(context, workerParams) {
    private val errorNotificationExists: Boolean
        get() = this.runAttemptCount > 0

    override suspend fun doWork(): Result {
        val oldSchedules = scheduleRepository.schedule()

        if (oldSchedules.isNotEmpty() && isNightNow()) {
            return Result.success()
        }

        val userId = userRepository.id()
        val (startDate, endDate) = scheduleRange()
        val newSchedulesResult = scheduleRepository.onWeek(userId, startDate, endDate)

        if (newSchedulesResult.isFailure) {
            if (!errorNotificationExists) {
                ScheduleNotification(
                    title = resources.string(R.string.schedule_update_error),
                    text = resources.string(R.string.lets_try_again),
                    type = ScheduleNotification.Type.FAILED
                ).send(notificationChannel)
            }
            return Result.retry()
        }

        // @todo rename variables schedule to days where is needed
        val updatedSchedule = newSchedulesResult.getOrThrow().days

        val firstDiffedSchedule = updatedSchedule.map { schedule ->
            val foundMatches =
                oldSchedules.first().days.find { it.date == schedule.date } ?: kotlin.run {
                    return@map Day(
                        schedule.date,
                        schedule.lessons.map { it.copy(isAdded = true) })
                }

            val addedLessons =
                (schedule.lessons - foundMatches.lessons.toSet()).map { it.copy(isAdded = true) }
            val deletedLessons =
                (foundMatches.lessons - schedule.lessons.toSet()).map { it.copy(isDeleted = true) }
            // @todo do not create day with empty list
            Day(
                schedule.date,
                // @todo sorted by time start is dangerous, because timestart is string
                (addedLessons + deletedLessons).sortedBy { it.timeStart })
        }

        val secondDiffedSchedule = mutableListOf<Day>()

        oldSchedules.first().days.forEach { schedule ->
            updatedSchedule.find { it.date == schedule.date } ?: kotlin.run {
                secondDiffedSchedule.add(
                    Day(
                        schedule.date,
                        schedule.lessons.map { it.copy(isDeleted = true) })
                )
            }
        }

        val resultDiffedSchedule = firstDiffedSchedule + secondDiffedSchedule

        notificationChannel.cancelFailedNotifications()

        scheduleRepository.deleteSchedule()
        scheduleRepository.save(newSchedulesResult.getOrThrow())

        if (oldSchedules.isEmpty()) {
            ScheduleNotification(
                title = resources.string(R.string.schedule_is_synchronized),
                text = resources.string(R.string.how_application_works)
            ).send(notificationChannel)
        } else if (resultDiffedSchedule.isNotEmpty()) {
            ScheduleNotification(
                title = resources.string(R.string.schedule_updated),
                text = resources.string(R.string.tap_to_view_schedule)
            ).send(notificationChannel)
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
