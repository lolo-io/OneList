package com.lolo.io.onelist.core.testing.fake

import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

val fakeSharedPreferenceHelper = FakeSharedPreferenceHelper()
class FakeSharedPreferenceHelper() : SharedPreferencesHelper {
    override var backupDisplayPath: String? = null
    override var backupUri: String? = null
    override var version: String = "1.0.0"
    override var theme: String = "light"
    override var firstLaunch: Boolean = false
    override var preferUseFiles: Boolean = false
    override var selectedListIndex: Int = 0
    private val _selectedListIndexStateFlow = MutableStateFlow(selectedListIndex)
    override val selectedListIndexStateFlow: StateFlow<Int>
        get() = _selectedListIndexStateFlow.asStateFlow()
    override val canAccessBackupUri: Boolean = true
}