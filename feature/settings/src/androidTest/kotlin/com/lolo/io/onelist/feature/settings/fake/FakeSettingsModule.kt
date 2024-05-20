package com.lolo.io.onelist.feature.settings.fake

import com.lolo.io.onelist.feature.settings.fragment.SettingsFragmentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val fakeSettingsModule = module {
    viewModel<SettingsFragmentViewModel>{
        SettingsFragmentViewModel(get(), get())
    }
}