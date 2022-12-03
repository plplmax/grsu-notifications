package com.github.plplmax.grsunotifications.data

interface LocalUserDataSource {
    suspend fun id(): Int
    fun saveId(id: Int)
    fun deleteId()
    suspend fun login(): String
    fun saveLogin(login: String)
}
