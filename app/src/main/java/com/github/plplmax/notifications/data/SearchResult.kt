package com.github.plplmax.notifications.data

data class SearchResult<T : Any>(val items: List<T>, val total: Long)
