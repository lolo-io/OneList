package com.lolo.io.onelist.di

import com.lolo.io.onelist.MainActivityViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel<MainActivityViewModel> {
        MainActivityViewModel(get(), get(), get())
    }
}

