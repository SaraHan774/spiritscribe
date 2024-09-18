package com.august.spiritscribe.utils

import android.app.Application

object ResourceUtils {
    private lateinit var application: Application

    fun init(application: Application) {
        this.application = application
    }

    fun getString(resourceId: Int): String {
        return application.getString(resourceId)
    }

    fun getStringArray(resourceId: Int): Array<String> {
        return application.resources.getStringArray(resourceId)
    }
}