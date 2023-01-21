package com.github.plplmax.grsunotifications.data.user.remote

interface RemoteUserDataSource {
    suspend fun idByLogin(login: String): Int
}
