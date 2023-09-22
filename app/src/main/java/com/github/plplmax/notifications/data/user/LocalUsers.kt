package com.github.plplmax.notifications.data.user

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.github.plplmax.notifications.data.database.Database
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalUsers(
    context: Context,
    private val origin: Users,
    private val database: Database,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : Users by origin {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override suspend fun id(): Int = withContext(dispatcher) {
        prefs.getInt(PREFS_NAME, 0)
    }

    override suspend fun saveId(id: Int) {
        prefs.edit { putInt(PREFS_NAME, id) }
    }

    override suspend fun deleteId() {
        prefs.edit { remove(PREFS_NAME) }
    }

    override suspend fun login(): String = withContext(dispatcher) {
        prefs.getString(LOGIN_KEY, "")!!
    }

    override suspend fun saveLogin(login: String) {
        prefs.edit { putString(LOGIN_KEY, login) }
    }

    override suspend fun signOut() {
        withContext(dispatcher) {
            database.instance().use { realm ->
                realm.executeTransaction { it.deleteAll() }
            }
            deleteId()
        }
    }

    companion object {
        private const val PREFS_NAME = "user"
        private const val LOGIN_KEY = "login"
    }
}
