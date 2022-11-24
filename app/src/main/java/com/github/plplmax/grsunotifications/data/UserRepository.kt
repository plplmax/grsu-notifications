package com.github.plplmax.grsunotifications.data

interface UserRepository {
    suspend fun idByLogin(login: String): Result<Int>
    suspend fun id(): Int
    fun saveId(id: Int)
}
