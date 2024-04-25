package com.lolo.io.onelist.feature.settings.fragment

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import com.lolo.io.onelist.core.domain.use_cases.OneListUseCases
import com.lolo.io.onelist.core.model.ItemList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsFragmentViewModel(
    private val useCases: OneListUseCases,
    private val preferences: SharedPreferencesHelper
) : ViewModel() {


    private var _backupDisplayPath = MutableStateFlow<String?>(null)
    val backupDisplayPath = _backupDisplayPath.asStateFlow()

    val preferUseFiles
        get() = preferences.preferUseFiles

    val version: String
        get() = preferences.version

    init {
        _backupDisplayPath.value = preferences.backupDisplayPath
    }

    fun setBackupPath(uri: Uri?, displayPath: String? = null) {
        if(uri == null) {
            preferences.preferUseFiles = false
        }
        viewModelScope.launch {
            useCases.setBackupUri(uri, displayPath)
            _backupDisplayPath.value = displayPath
        }
    }

    suspend fun importList(uri: Uri): ItemList {
        return useCases.importList(uri)
    }

    suspend fun backupAllListsOnDevice() {
        useCases.syncAllLists()
    }

    fun onPreferUseFiles() {
        viewModelScope.launch {
            useCases.loadAllLists()
        }
    }

    val syncFolderNotAccessible
        get() = !preferences.canAccessBackupUri
}