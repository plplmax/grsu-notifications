package com.github.plplmax.grsunotifications.data.impl

import com.github.plplmax.grsunotifications.data.Errors
import com.github.plplmax.grsunotifications.data.LocalUserDataSource
import com.github.plplmax.grsunotifications.data.RemoteUserDataSource
import com.github.plplmax.grsunotifications.data.UserRepository
import com.github.plplmax.grsunotifications.data.result.NetworkResultImpl

class UserRepositoryImpl(
    private val remote: RemoteUserDataSource,
    private val local: LocalUserDataSource
) : UserRepository {
    override suspend fun idByLogin(login: String): Result<Int> {
        return kotlin.runCatching {
            val userId = NetworkResultImpl { remote.idByLogin(login) }.result().getOrThrow()
            if (userId == 0) {
                error(Errors.INVALID_LOGIN)
            }
            userId
        }
    }

    override suspend fun id(): Int = local.id()

    override fun saveId(id: Int) {
        local.saveId(id)
    }

    override fun deleteId() {
        local.deleteId()
    }

    override suspend fun login(): String = local.login()

    override fun saveLogin(login: String) {
        local.saveLogin(login)
    }
}
