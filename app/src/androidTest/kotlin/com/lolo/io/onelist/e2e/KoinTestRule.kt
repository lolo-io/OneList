package com.lolo.io.onelist.e2e

import androidx.test.platform.app.InstrumentationRegistry
import com.lolo.io.onelist.core.data.di.dataModule
import com.lolo.io.onelist.core.database.di.daosModule
import com.lolo.io.onelist.core.domain.di.domainModule
import com.lolo.io.onelist.di.appModule
import com.lolo.io.onelist.feature.lists.di.listsModule
import com.lolo.io.onelist.feature.settings.di.settingsModule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class KoinTestRule  : TestWatcher() {
    override fun starting(description: Description) {
        startKoin {
            androidContext(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext)
            fragmentFactory()
            modules(
                appModule,
                listsModule,
                settingsModule,
                dataModule,
                daosModule,
                domainModule
            )
        }
    }
    override fun finished(description: Description) {
        stopKoin()
    }
}