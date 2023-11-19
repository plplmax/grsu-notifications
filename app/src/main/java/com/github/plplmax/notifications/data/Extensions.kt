package com.github.plplmax.notifications.data

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.Url

val Url.originWithEncodedPath: String
    get() = buildString {
        append(protocol.name)
        append("://")
        append(host)
        append(encodedPath)
    }

fun HttpResponse.toStringForLogging(): String =
    "HttpResponse[${request.url.originWithEncodedPath}, $status]"
