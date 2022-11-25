package com.github.plplmax.grsunotifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.github.plplmax.grsunotifications.data.UserRepository
import com.github.plplmax.grsunotifications.data.workManager.ScheduleWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainViewModel(
    private val userRepository: UserRepository,
    private val workManager: WorkManager
) : ViewModel() {
    fun startUpdates(login: String) {
        viewModelScope.launch {
            val userId = userRepository.idByLogin(login)
            userId.onFailure { println("Failure: $it") }
            userId.onSuccess { id ->
                userRepository.saveId(id)
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                workManager.enqueueUniquePeriodicWork(
                    "ScheduleUpdate",
                    ExistingPeriodicWorkPolicy.REPLACE,
                    PeriodicWorkRequestBuilder<ScheduleWorker>(
                        30, TimeUnit.MINUTES
                    ).setConstraints(constraints).build()
                )
            }
        }
    }
}

fun <T : ViewModel> T.createFactory(): ViewModelProvider.Factory {
    val viewModel = this
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = viewModel as T
    }
}
