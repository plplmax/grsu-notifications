package com.github.plplmax.notifications.data.database

import io.realm.Realm

class RealmDatabase : Database {
    override fun instance(): Realm = Realm.getDefaultInstance()
}