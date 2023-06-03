package com.github.plplmax.notifications.data.database

import io.realm.Realm

interface Database {
    fun instance(): Realm
}