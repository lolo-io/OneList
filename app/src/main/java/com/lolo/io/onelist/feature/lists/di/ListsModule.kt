package com.lolo.io.onelist.feature.lists.di

import com.lolo.io.onelist.feature.lists.OneListFragmentViewModel
import com.lolo.io.onelist.feature.lists.tuto.FirstLaunchLists
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val listsModule = module {
    viewModel<OneListFragmentViewModel> {
        OneListFragmentViewModel(get(), get(), get())
    }
    single {
        FirstLaunchLists(get())
    }
}