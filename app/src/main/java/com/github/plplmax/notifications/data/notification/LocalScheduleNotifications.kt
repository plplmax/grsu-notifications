package com.github.plplmax.notifications.data.notification

import com.github.plplmax.notifications.data.database.Database
import com.github.plplmax.notifications.data.notification.models.ScheduleDiffNotification
import com.github.plplmax.notifications.data.notification.models.ScheduleDiffNotificationRealm
import com.github.plplmax.notifications.data.notification.models.ShortScheduleDiffNotification
import com.github.plplmax.notifications.data.notification.models.toData
import com.github.plplmax.notifications.data.notification.models.toRealm
import com.github.plplmax.notifications.data.notification.models.toShortData
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId

class LocalScheduleNotifications(
    private val database: Database,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ScheduleNotifications {
    override suspend fun save(notification: ScheduleDiffNotification) {
        withContext(ioDispatcher) {
            database.instance().use { realm ->
                realm.executeTransaction { it.insert(notification.toRealm()) }
            }
        }
    }

    override suspend fun deleteById(id: String) {
        withContext(ioDispatcher) {
            database.instance().use { realm ->
                realm.executeTransaction {
                    it.where<ScheduleDiffNotificationRealm>()
                        .equalTo("id", ObjectId(id))
                        .findAll()
                        .deleteAllFromRealm()
                }
            }
        }
    }

    override suspend fun notificationById(id: String): List<ScheduleDiffNotification> {
        return withContext(ioDispatcher) {
            database.instance().use { realm ->
                realm.where<ScheduleDiffNotificationRealm>()
                    .equalTo("id", ObjectId(id))
                    .findAll()
                    .let(realm::copyFromRealm)
                    .map(ScheduleDiffNotificationRealm::toData)
            }
        }
    }

    override suspend fun notifications(): List<ShortScheduleDiffNotification> {
        return withContext(ioDispatcher) {
            database.instance().use { realm ->
                realm.where<ScheduleDiffNotificationRealm>()
                    .findAll()
                    .sort("created", Sort.DESCENDING)
                    .let(realm::copyFromRealm)
                    .map(ScheduleDiffNotificationRealm::toShortData)
            }
        }
    }

    override suspend fun read(id: String) {
        return withContext(ioDispatcher) {
            database.instance().use { realm ->
                realm.executeTransaction {
                    it.where<ScheduleDiffNotificationRealm>()
                        .equalTo("id", ObjectId(id))
                        .findFirst()
                        ?.read = true
                }
            }
        }
    }
}
