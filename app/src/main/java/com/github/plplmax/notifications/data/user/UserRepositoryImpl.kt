package com.github.plplmax.notifications.data.user

import com.github.plplmax.notifications.data.Errors
import com.github.plplmax.notifications.data.result.NetworkResultImpl
import com.github.plplmax.notifications.data.user.local.LocalUserDataSource
import com.github.plplmax.notifications.data.user.remote.RemoteUserDataSource

class UserRepositoryImpl(
    private val remote: RemoteUserDataSource,
    private val local: LocalUserDataSource
) : UserRepository {
    override suspend fun idByLogin(login: String): Result<Int> {
        val result = NetworkResultImpl { remote.idByLogin(login) }.result()
        return result.mapCatching { userId ->
            if (userId == 0) error(Errors.INVALID_LOGIN)
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
