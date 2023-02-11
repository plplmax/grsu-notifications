package com.github.plplmax.notifications.data.user.remote

interface RemoteUserDataSource {
    suspend fun idByLogin(login: String): Int
}
