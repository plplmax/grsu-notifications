package com.github.plplmax.notifications.data.user

import com.github.plplmax.notifications.data.toStringForLogging
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException

class RemoteUsers(
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : Users {
    override suspend fun idByLogin(login: String): Result<Int> {
        return withContext(dispatcher) {
            try {
                val response = client.get("getStudent") { parameter("login", login) }
                if (!response.status.isSuccess()) throw IOException("Unexpected code ${response.toStringForLogging()}")
                val body = response.bodyAsText()
                val id = JSONObject(body).getInt("id")
                Result.success(id)
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                Result.failure(e)
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

    override suspend fun signOut() {
        // do nothing
    }
}
