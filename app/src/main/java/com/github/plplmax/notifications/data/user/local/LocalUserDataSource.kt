package com.github.plplmax.notifications.data.user.local

interface LocalUserDataSource {
    suspend fun id(): Int
    fun saveId(id: Int)
    fun deleteId()
    suspend fun login(): String
    fun saveLogin(login: String)
}
