package com.lolo.io.onelist.core.testing.rules

import android.app.Application
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module

class KoinTestRule(private val koinModules: List<Module>) : TestWatcher() {
    override fun starting(description: Description) {
        startKoin {
            androidContext(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext)
            fragmentFactory()
            modules(koinModules)
        }
    }

    override fun finished(description: Description) {
        stopKoin()
    }
}