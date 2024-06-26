package com.lolo.io.onelist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import com.lolo.io.onelist.core.domain.use_cases.OneListUseCases
import com.lolo.io.onelist.core.model.FirstLaunchLists
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val firstLaunchLists: FirstLaunchLists,
    private val useCases: OneListUseCases,
    private val preferences: SharedPreferencesHelper,
) : ViewModel() {

    private val _showWhatsNew = MutableStateFlow(false)
    val showWhatsNew = _showWhatsNew.asStateFlow()
    private val _listsLoaded = MutableStateFlow(false)
    val listsLoaded = _listsLoaded.asStateFlow()

    fun init() {
        viewModelScope.launch {
            useCases.handleFirstLaunch(firstLaunchLists.firstLaunchLists())
            setAppVersion()
            useCases.loadAllLists().first()
            _listsLoaded.value = true
        }
    }

    private fun setAppVersion() {
        if (preferences.version != BuildConfig.VERSION_NAME) {
            _showWhatsNew.value = useCases.shouldShowWhatsNew(BuildConfig.VERSION_NAME)
            preferences.version = BuildConfig.VERSION_NAME
        }
    }
}