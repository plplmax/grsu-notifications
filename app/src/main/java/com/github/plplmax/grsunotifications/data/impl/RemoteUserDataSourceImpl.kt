package com.github.plplmax.grsunotifications.data.impl

import com.github.plplmax.grsunotifications.data.Constants
import com.github.plplmax.grsunotifications.data.RemoteUserDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class RemoteUserDataSourceImpl(
    private val client: OkHttpClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : RemoteUserDataSource {
    override suspend fun idByLogin(login: String): Int = withContext(dispatcher) {
        val request = Request.Builder()
            .url("${Constants.BASE_URL}/getStudent?login=$login")
            .build()

        suspendCoroutine { continuation ->
            client.newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        continuation.resumeWithException(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")

                            val userId = JSONObject(response.body!!.string()).getInt("id")
                            continuation.resume(userId)
                        }
                    }
                })
        }
    }
}
