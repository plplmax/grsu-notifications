package com.github.plplmax.notifications.data.notification.paging

import androidx.paging.PagingSource

interface PagingLoad<Key : Any, Value : Any> {
    suspend fun initial(loadSize: Long): PagingSource.LoadResult<Key, Value>
    suspend fun refresh(
        key: Key,
        loadSize: Long
    ): PagingSource.LoadResult<Key, Value>

    suspend fun append(
        key: Key,
        loadSize: Long
    ): PagingSource.LoadResult<Key, Value>

    suspend fun prepend(
        key: Key,
        loadSize: Long
    ): PagingSource.LoadResult<Key, Value>
}
