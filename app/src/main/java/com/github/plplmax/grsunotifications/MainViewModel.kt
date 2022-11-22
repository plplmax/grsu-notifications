package com.github.plplmax.grsunotifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.plplmax.grsunotifications.data.ScheduleRepository
import com.github.plplmax.grsunotifications.data.UserRepository
import com.github.plplmax.grsunotifications.data.impl.RemoteScheduleDataSourceImpl
import com.github.plplmax.grsunotifications.data.impl.RemoteUserDataSourceImpl
import com.github.plplmax.grsunotifications.data.impl.ScheduleRepositoryImpl
import com.github.plplmax.grsunotifications.data.impl.UserRepositoryImpl
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(
    httpClient: OkHttpClient = OkHttpClient(),
    private val userRepository: UserRepository = UserRepositoryImpl(
        RemoteUserDataSourceImpl(
            httpClient
        )
    ),
    private val scheduleRepository: ScheduleRepository = ScheduleRepositoryImpl(
        RemoteScheduleDataSourceImpl(httpClient)
    )
) : ViewModel() {
    fun startUpdates(login: String) {
        viewModelScope.launch {
            val userId = userRepository.idByLogin(login)
            userId.onFailure { println("Failure: $it") }
            userId.onSuccess { id ->
                val (startDate, endDate) = scheduleRange()
                val jsonSchedule = scheduleRepository.onWeek(id, startDate, endDate)

                jsonSchedule.onFailure { println("Schedule failure") }
                jsonSchedule.onSuccess { println(it) }
            }
        }
    }

    private fun scheduleRange(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
        return dateFormat.format(calendar.time).let { startDate ->
            calendar.add(Calendar.DATE, 6)
            Pair(startDate, dateFormat.format(calendar.time))
        }
    }
}
