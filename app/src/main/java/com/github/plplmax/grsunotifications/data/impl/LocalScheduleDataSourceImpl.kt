package com.github.plplmax.grsunotifications.data.impl

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.github.plplmax.grsunotifications.data.LocalScheduleDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalScheduleDataSourceImpl(
    context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : LocalScheduleDataSource {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun saveScheduleHash(hash: String) {
        prefs.edit { putString(PREFS_NAME, hash) }
    }

    override suspend fun scheduleHash(): String = withContext(dispatcher) {
        prefs.getString(PREFS_NAME, "")!!
    }

    companion object {
        private const val PREFS_NAME = "schedule"
    }
}
