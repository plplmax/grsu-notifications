package com.github.plplmax.grsunotifications.data.impl

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.github.plplmax.grsunotifications.data.LocalUserDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalUserDataSourceImpl(
    context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : LocalUserDataSource {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override suspend fun id(): Int = withContext(dispatcher) {
        prefs.getInt(PREFS_NAME, 0)
    }

    override fun saveId(id: Int) {
        prefs.edit { putInt(PREFS_NAME, id) }
    }

    companion object {
        private const val PREFS_NAME = "user"
    }
}
