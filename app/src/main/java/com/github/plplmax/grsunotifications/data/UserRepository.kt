package com.github.plplmax.grsunotifications.data

interface UserRepository {
    suspend fun idByLogin(login: String): Result<Int>
}
