package com.github.plplmax.notifications.data.notification

import com.github.plplmax.notifications.data.SearchResult
import com.github.plplmax.notifications.data.database.Database
import com.github.plplmax.notifications.data.notification.models.ScheduleDiffNotification
import com.github.plplmax.notifications.data.notification.models.ScheduleDiffNotificationRealm
import com.github.plplmax.notifications.data.notification.models.ShortScheduleDiffNotification
import com.github.plplmax.notifications.data.notification.models.toData
import com.github.plplmax.notifications.data.notification.models.toRealm
import com.github.plplmax.notifications.data.notification.models.toShortData
import com.github.plplmax.notifications.data.Comparison
import io.realm.OrderedCollectionChangeSet
import io.realm.Sort
import io.realm.kotlin.toChangesetFlow
import io.realm.kotlin.where
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId
import java.util.Date

class LocalScheduleNotifications(
    private val database: Database,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ScheduleNotifications {
    override suspend fun save(notification: ScheduleDiffNotification) {
        withContext(ioDispatcher) {
            database.instance().use { realm ->
                realm.executeTransaction { it.insertOrUpdate(notification.toRealm()) }
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

    override suspend fun notifications(
        date: Date,
        limit: Long,
        comparison: Comparison,
        sort: Sort
    ): SearchResult<ShortScheduleDiffNotification> = withContext(ioDispatcher) {
        database.instance().use { realm ->
            val query = realm.where<ScheduleDiffNotificationRealm>()
            val field = "created"

            when (comparison) {
                Comparison.LESS -> query.lessThan(field, date)
                Comparison.LESS_OR_EQUAL -> query.lessThanOrEqualTo(field, date)
                Comparison.GREATER -> query.greaterThan(field, date)
                Comparison.GREATER_OR_EQUAL -> query.greaterThanOrEqualTo(field, date)
            }

            val total = query.count()
            val items = query.sort(field, sort)
                .limit(limit)
                .findAll()
                .let(realm::copyFromRealm)
                .map(ScheduleDiffNotificationRealm::toShortData)

            SearchResult(items = items, total = total)
        }
    }

    override suspend fun read(id: String) {
        notificationById(id).map { it.copy(read = true) }.forEach { save(it) }
    }

    override fun changesetFlow(): Flow<Boolean> {
        return flow {
            database.instance().let { realm ->
                realm.where<ScheduleDiffNotificationRealm>()
                    .findAllAsync()
                    .toChangesetFlow()
                    .filter { it.changeset?.state == OrderedCollectionChangeSet.State.UPDATE }
                    .map { true }
                    .onCompletion { realm.close() }
                    .let { emitAll(it) }
            }
        }
    }
}
