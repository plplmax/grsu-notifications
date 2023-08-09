package com.github.plplmax.notifications.data.user

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import timber.log.Timber

class LoggedUsers(
    private val origin: Users,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : Users {
    override suspend fun idByLogin(login: String): Result<Int> {
        return withContext(dispatcher) {
            origin.idByLogin(login)
                .onFailure(Timber::e)
        }
    }

    override suspend fun id(): Int {
        return withContext(dispatcher) {
            try {
                origin.id()
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                Timber.e(e)
                throw e
            }
        }
    }

    override suspend fun saveId(id: Int) {
        withContext(dispatcher) {
            try {
                origin.saveId(id)
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                Timber.e(e)
                throw e
            }
        }
    }

    override suspend fun deleteId() {
        withContext(dispatcher) {
            try {
                origin.deleteId()
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                Timber.e(e)
                throw e
            }
        }
    }

    override suspend fun login(): String {
        return withContext(dispatcher) {
            try {
                origin.login()
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                Timber.e(e)
                throw e
            }
        }
    }

    override suspend fun saveLogin(login: String) {
        withContext(dispatcher) {
            try {
                origin.saveLogin(login)
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                Timber.e(e)
                throw e
            }
        }
    }
}
