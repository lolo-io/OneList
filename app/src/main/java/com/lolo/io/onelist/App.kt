package com.lolo.io.onelist

import android.app.Application
import com.lolo.io.onelist.core.data.di.dataModule
import com.lolo.io.onelist.di.appModule
import com.lolo.io.onelist.core.database.di.daosModule
import com.lolo.io.onelist.core.domain.di.domainModule
import com.lolo.io.onelist.feature.lists.di.listsModule
import com.lolo.io.onelist.feature.settings.di.settingsModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            fragmentFactory()
            modules(
                appModule,
                listsModule,
                settingsModule,
                dataModule,
                daosModule,
                domainModule)
        }
    }
}