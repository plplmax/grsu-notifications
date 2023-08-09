package com.github.plplmax.notifications.data.user

import com.github.plplmax.notifications.data.Constants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class RemoteUsers(
    private val client: OkHttpClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : Users {
    override suspend fun idByLogin(login: String): Result<Int> {
        return withContext(dispatcher) {
            kotlin.runCatching {
                val request = Request.Builder()
                    .url("${Constants.BASE_URL}/getStudent?login=$login")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    JSONObject(response.body!!.string()).getInt("id")
                }
            }
        }
    }

    override suspend fun id(): Int = 0

    override suspend fun saveId(id: Int) {
        // do nothing
    }

    override suspend fun deleteId() {
        // do nothing
    }

    override suspend fun login(): String = ""

    override suspend fun saveLogin(login: String) {
        // do nothing
    }
}
