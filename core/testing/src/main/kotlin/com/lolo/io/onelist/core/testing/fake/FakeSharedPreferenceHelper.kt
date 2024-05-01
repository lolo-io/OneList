package com.lolo.io.onelist.core.testing.fake

import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSharedPreferenceHelper : SharedPreferencesHelper {
    override var backupDisplayPath: String? = null
    override var backupUri: String? = null
    override var version: String = "1.0.0"
    override var theme: String = "light"
    override var firstLaunch: Boolean = false
    override var preferUseFiles: Boolean = false
    override var selectedListIndex: Int
        get() = _selectedListIndexStateFlow.value
        set(value) {
            _selectedListIndexStateFlow.value = value
        }
    private val _selectedListIndexStateFlow = MutableStateFlow(0)
    override val selectedListIndexStateFlow: StateFlow<Int>
        get() = _selectedListIndexStateFlow.asStateFlow()
    override var canAccessBackupUri: Boolean = true
    private set // bc interface is a val


    fun setCanAccessBackupUri(can: Boolean) {
        canAccessBackupUri = can
    }

    fun tearDown() {
        backupUri = null
        preferUseFiles = false
        version = "1.0.0"
        theme = "light"
        firstLaunch = false
        _selectedListIndexStateFlow.value = 0
        canAccessBackupUri = true

    }
}