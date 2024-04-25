package com.lolo.io.onelist.core.data.di

import com.lolo.io.onelist.core.data.file_access.FileAccess
import com.lolo.io.onelist.core.data.reporitory.OneListRepository
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import org.koin.dsl.module

val dataModule = module {

    single {
        SharedPreferencesHelper(get())
    }

    single {
        OneListRepository(get(), get(), get())
    }

    single {
        FileAccess(get())
    }
}

