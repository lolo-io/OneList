package com.lolo.io.onelist.feature.lists.di

import com.lolo.io.onelist.core.data.migration.UpdateHelper
import com.lolo.io.onelist.feature.lists.ListScreenViewModel
import com.lolo.io.onelist.feature.lists.tuto.FirstLaunchLists
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val listsModule = module {
    viewModel<ListScreenViewModel> {
        ListScreenViewModel(get(), get())
    }

    single {
        UpdateHelper(get(), get())
    }
}