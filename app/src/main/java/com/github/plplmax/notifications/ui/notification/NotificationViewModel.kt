package com.github.plplmax.notifications.ui.notification

import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.plplmax.notifications.data.notification.ScheduleNotifications
import com.github.plplmax.notifications.data.notification.models.ShortScheduleDiffNotification
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class NotificationViewModel(
    private val notifications: ScheduleNotifications,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
    private var cachedNotifications: MutableState<Map<LocalDate, List<ShortScheduleDiffNotification>>> =
        mutableStateOf(emptyMap())
    var uiState: UiState by mutableStateOf(UiState.Loading)
        private set

    val flow: Flow<PagingData<ShortScheduleDiffNotification>> =
        Pager(config = PagingConfig(pageSize = 10, enablePlaceholders = false)) {
            NotificationPagingSource(notifications, viewModelScope)
        }.flow.cachedIn(viewModelScope)

//    init {
//        loadNotifications()
//    }

    init {
        println("instantiate NotifViewModel")
    }

//    init {
//        viewModelScope.launch {
//            repeat(100) {
//                notifications.save(
//                    ScheduleDiffNotification(created = ZonedDateTime.now().minusDays(it.toLong()))
//                )
//            }
//            println("repeat completed")
//        }
//    }

    fun loadNotifications() {
//        uiState = UiState.Loading
//        viewModelScope.launch {
//            uiState = try {
//                val result = withContext(ioDispatcher) {
//                    notifications.notifications().groupBy { it.created.toLocalDate() }
//                }
//                cachedNotifications.value = result
//                UiState.Loaded(cachedNotifications)
//            } catch (_: Exception) {
//                currentCoroutineContext().ensureActive()
//                UiState.Error(message = R.string.something_went_wrong)
//            }
//        }
    }

    fun deleteNotificationAsync(id: String): Deferred<Boolean> {
        return viewModelScope.async {
            try {
                withContext(ioDispatcher) { notifications.deleteById(id) }
                // @todo maybe add mutex to prevent data race when user deletes multiple notifications
//                cachedNotifications.value = cachedNotifications.value.toMutableMap().apply {
//                    computeIfPresent(date) { _, notifications ->
//                        val updatedNotifications = notifications.filterNot { it.id == id }
//                        updatedNotifications.ifEmpty { null }
//                    }
//                }
                true
            } catch (_: Exception) {
                currentCoroutineContext().ensureActive()
                false
            }
        }
    }

    fun readNotification(date: LocalDate, id: String) {
        viewModelScope.launch {
            try {
                withContext(ioDispatcher) { notifications.read(id) }
                cachedNotifications.value = cachedNotifications.value.toMutableMap().apply {
                    computeIfPresent(date) { _, notifications ->
                        notifications.map {
                            if (it.id == id) it.copy(read = true)
                            else it
                        }
                    }
                }
            } catch (_: Exception) {
                // @todo think about handling exceptions
            }
        }
    }

    sealed class UiState {
        // do not add data keyword for Error class because snackbar
        // with retry button does not appear due to possible
        // fast transition: Error -> Loading -> Error
        // As a result, Loading phase may be skipped and compose
        // does not detect any changes in the state if the two
        // errors have the same message.
        class Error(@StringRes val message: Int) : UiState()
        object Loading : UiState()
        data class Loaded(
            val notifications: Flow<PagingData<ShortScheduleDiffNotification>>
        ) : UiState()
    }
}
