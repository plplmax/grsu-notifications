package com.github.plplmax.notifications.data.workManager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.plplmax.notifications.R
import com.github.plplmax.notifications.data.diffedSchedule.DiffedScheduleRepository
import com.github.plplmax.notifications.data.schedule.ScheduleRepository
import com.github.plplmax.notifications.data.schedule.models.Day
import com.github.plplmax.notifications.data.schedule.models.DiffedSchedule
import com.github.plplmax.notifications.data.schedule.models.Schedule
import com.github.plplmax.notifications.data.user.UserRepository
import com.github.plplmax.notifications.notification.ScheduleDiffNotification
import com.github.plplmax.notifications.notification.NotificationType
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
    private val diffedScheduleRepository: DiffedScheduleRepository,
    private val notificationChannel: ScheduleNotificationChannel,
    private val resources: Resources
) : CoroutineWorker(context, workerParams) {
    private val errorNotificationExists: Boolean
        get() = this.runAttemptCount > 0

    override suspend fun doWork(): Result {
        val oldScheduleResult = scheduleRepository.schedule()

        if (oldScheduleResult.isNotEmpty() && isNightNow()) {
            return Result.success()
        }

        val userId = userRepository.id()
        val (startDate, endDate) = scheduleRange()
        val newScheduleResult = scheduleRepository.onWeek(userId, startDate, endDate)

        if (newScheduleResult.isFailure) {
            if (!errorNotificationExists) {
                ScheduleNotification(
                    title = resources.string(R.string.schedule_update_error),
                    text = resources.string(R.string.lets_try_again),
                    type = NotificationType.FAILED
                ).send(notificationChannel)
            }
            return Result.retry()
        }

        val oldSchedule = oldScheduleResult.firstOrNull() ?: Schedule(days = listOf())
        val newSchedule = newScheduleResult.getOrThrow()

        val daysDiffNewToOld = newSchedule.days.map { newDay ->
            val foundMatches =
                oldSchedule.days.find { it.date == newDay.date } ?: kotlin.run {
                    return@map newDay.copy(lessons = newDay.lessons.map { it.copy(isAdded = true) })
                }

            val addedLessons =
                (newDay.lessons - foundMatches.lessons.toSet()).map { it.copy(isAdded = true) }
            val deletedLessons =
                (foundMatches.lessons - newDay.lessons.toSet()).map { it.copy(isDeleted = true) }
            newDay.copy(
                lessons = (addedLessons + deletedLessons).sortedBy { it.timeStart })
        }

        val daysDiffOldToNew = mutableListOf<Day>()

        oldSchedule.days.forEach { oldDay ->
            newSchedule.days.find { it.date == oldDay.date } ?: kotlin.run {
                daysDiffOldToNew.add(
                    oldDay.copy(lessons = oldDay.lessons.map { it.copy(isDeleted = true) })
                )
            }
        }

        // @todo sort days for date (try to swap addition arguments)
        val diffedSchedule = DiffedSchedule(
            days = daysDiffNewToOld.filter { it.lessons.isNotEmpty() } + daysDiffOldToNew
        )

        notificationChannel.cancelFailedNotifications()

        scheduleRepository.deleteSchedule()
        scheduleRepository.save(newSchedule)
        val diffId = diffedScheduleRepository.save(diffedSchedule)

        if (oldScheduleResult.isEmpty()) {
            ScheduleNotification(
                title = resources.string(R.string.schedule_is_synchronized),
                text = resources.string(R.string.how_application_works)
            ).send(notificationChannel)
        } else if (diffedSchedule.days.isNotEmpty()) {
            ScheduleDiffNotification(
                id = diffId,
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
