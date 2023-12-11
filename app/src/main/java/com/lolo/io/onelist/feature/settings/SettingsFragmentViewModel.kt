package com.lolo.io.onelist.feature.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lolo.io.onelist.core.data.persistence.PersistenceHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class SettingsFragmentViewModel(
    val persistence: PersistenceHelper
) : ViewModel() {


    private var _backupDisplayPath = MutableStateFlow<String?>("")
    val backupDisplayPath = _backupDisplayPath.asStateFlow()

    val version: String
        get() = persistence.version

    init {
        _backupDisplayPath.value = persistence.backupDisplayPath
    }

    fun getTheme()  = persistence.theme
    fun setTheme(theme: String) {
        persistence.theme = theme
    }

    fun setBackupUri(uri: Uri, displayPath: String) {
        viewModelScope.launch {
            persistence.backupUri = uri.toString()
            persistence.backupDisplayPath = displayPath
            _backupDisplayPath.value = displayPath
        }
    }

    fun deleteBackupUri() {
        viewModelScope.launch {
            persistence.backupUri = null
            persistence.backupDisplayPath = null
            _backupDisplayPath.value = null
        }
    }
}