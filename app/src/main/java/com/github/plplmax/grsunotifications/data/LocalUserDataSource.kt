package com.github.plplmax.grsunotifications.data

interface LocalUserDataSource {
    suspend fun id(): Int
    fun saveId(id: Int)
}
