package com.lolo.io.onelist.core.testing.fake

import com.lolo.io.onelist.core.data.file_access.FileAccess
import com.lolo.io.onelist.core.data.repository.OneListRepository
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import org.koin.dsl.module

fun fakeDataModule(
    repository: FakeOneListRepository = FakeOneListRepository(),
    sharedPreferencesHelper: SharedPreferencesHelper = FakeSharedPreferenceHelper(),
    fileAccess: FileAccess = FakeFileAccess()
) = module {

    single<SharedPreferencesHelper> {
        sharedPreferencesHelper
    }

    single<OneListRepository> {
        repository
    }

    single {
        fileAccess
    }
}

