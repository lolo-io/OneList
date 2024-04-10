package com.lolo.io.onelist.feature.lists.di

import com.lolo.io.onelist.core.data.migration.UpdateHelper
import com.lolo.io.onelist.feature.lists.OneListScreenViewModel
import com.lolo.io.onelist.feature.lists.tuto.FirstLaunchLists
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val listsModule = module {
    viewModel<OneListScreenViewModel> {
        OneListScreenViewModel(get(), get(), get())
    }
    single {
        FirstLaunchLists(get())
    }

    single {
        UpdateHelper(get(), get())
    }
}