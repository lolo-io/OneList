package com.lolo.io.onelist

import android.app.Application
import android.content.Context

class App : Application() {
    val context: Context
    get() = applicationContext

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    companion object {
        lateinit var instance: App
    }
}