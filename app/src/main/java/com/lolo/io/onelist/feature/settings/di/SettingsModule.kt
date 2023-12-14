package com.lolo.io.onelist.feature.settings.di

import com.lolo.io.onelist.feature.settings.SettingsFragmentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    viewModel<SettingsFragmentViewModel>{
        SettingsFragmentViewModel(get(), get())
    }
}