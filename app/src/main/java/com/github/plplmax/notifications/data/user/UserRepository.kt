package com.github.plplmax.notifications.data.user

interface UserRepository {
    suspend fun idByLogin(login: String): Result<Int>
    suspend fun id(): Int
    fun saveId(id: Int)
    fun deleteId()
    suspend fun login(): String
    fun saveLogin(login: String)
}
