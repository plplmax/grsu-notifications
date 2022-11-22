package com.github.plplmax.grsunotifications.data

interface RemoteUserDataSource {
    suspend fun idByLogin(login: String): Int
}
