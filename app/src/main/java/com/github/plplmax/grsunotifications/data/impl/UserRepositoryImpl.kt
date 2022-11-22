package com.github.plplmax.grsunotifications.data.impl

import com.github.plplmax.grsunotifications.data.Errors
import com.github.plplmax.grsunotifications.data.RemoteUserDataSource
import com.github.plplmax.grsunotifications.data.UserRepository
import java.net.UnknownHostException

class UserRepositoryImpl(private val remote: RemoteUserDataSource) : UserRepository {
    override suspend fun idByLogin(login: String): Result<Int> {
        return kotlin.runCatching {
            try {
                remote.idByLogin(login)
            } catch (e: Exception) {
                when (e) {
                    is UnknownHostException -> error(Errors.CHECK_INTERNET_CONNECTION)
                    else -> {
                        println(e)
                        error(Errors.GENERIC_ERROR)
                    }
                }
            }
        }
    }
}
