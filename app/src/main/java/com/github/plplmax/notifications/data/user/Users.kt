package com.github.plplmax.notifications.data.user

interface Users {
    suspend fun idByLogin(login: String): Result<Int>
    suspend fun id(): Int
    suspend fun saveId(id: Int)
    suspend fun deleteId()
    suspend fun login(): String
    suspend fun saveLogin(login: String)
    suspend fun signOut()
}
