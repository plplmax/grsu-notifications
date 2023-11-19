package com.github.plplmax.notifications.data.user

import com.github.plplmax.notifications.data.Errors

class MappedUsers(private val origin: Users) : Users by origin {
    override suspend fun idByLogin(login: String): Result<Int> {
        return origin.idByLogin(login)
            .fold(
                onSuccess = { Result.success(it) },
                onFailure = { e -> Result.failure(Errors.from(e)) }
            )
            .mapCatching { userId ->
                if (userId == 0) error(Errors.INVALID_LOGIN)
                userId
            }
    }
}
