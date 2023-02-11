package com.github.plplmax.notifications.resources

import android.content.Context

class GrsuResources(private val context: Context) : Resources {
    override fun string(id: Int): String {
        return context.getString(id)
    }
}
