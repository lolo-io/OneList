package com.lolo.io.onelist.core.data.di

import com.lolo.io.onelist.core.data.file_access.FileAccess
import com.lolo.io.onelist.core.data.repository.OneListRepository
import com.lolo.io.onelist.core.data.repository.OneListRepositoryImpl
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelperImpl
import org.koin.dsl.module

val dataModule = module {

    single<SharedPreferencesHelper> {
        SharedPreferencesHelperImpl(get())
    }

    single<OneListRepository> {
        OneListRepositoryImpl(get(), get(), get())
    }

    single {
        FileAccess(get())
    }
}

