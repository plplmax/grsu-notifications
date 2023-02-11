package com.github.plplmax.notifications.data.user.remote

import com.github.plplmax.notifications.data.Constants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class RemoteUserDataSourceImpl(
    private val client: OkHttpClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : RemoteUserDataSource {
    override suspend fun idByLogin(login: String): Int = withContext(dispatcher) {
        val request = Request.Builder()
            .url("${Constants.BASE_URL}/getStudent?login=$login")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            JSONObject(response.body!!.string()).getInt("id")
        }
    }
}
