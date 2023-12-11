package com.lolo.io.onelist.core.ui

import android.content.Context
import android.content.res.Configuration

object Config {
    private var screenSize: Int = -1
    val smallScreen
        get() = screenSize <= Configuration.SCREENLAYOUT_SIZE_SMALL

    fun init(context: Context) {
        screenSize =
            context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK

    }
}