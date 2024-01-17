package com.lolo.io.onelist.core.data.shared_preferences

import android.app.Application
import android.net.Uri
import androidx.preference.PreferenceManager
import com.anggrayudi.storage.file.DocumentFileCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedPreferencesHelper(
    private val app: Application,
) {
    private val versionPref: String = "version"
    private val selectedListPref = "selectedList"
    private val backUpLocallyPref = "backUpLocally"
    private val backupDisplayPathPref = "backupDisplayPath" // only for display
    private val themePref: String = "theme"
    private val firstLaunchPref = "firstLaunch"
    private val preferUseFilesPref = "preferUseFiles"

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)

    private fun getPref(key: String, default: String? = null): String? {
        return sharedPreferences.getString(key, default) ?: default
    }

    private fun editPref(key: String, value: String?) {
        sharedPreferences.edit()
            .putString(key, value)
            .apply()
    }

    private fun String.getPref(default: Boolean): Boolean {
        return sharedPreferences.getBoolean(this, default)
    }

    private fun String.editPref(value: Boolean = false) {
        sharedPreferences.edit()
            .putBoolean(this, value)
            .apply()
    }

    private fun String.getPref(default: Int): Int {
        return sharedPreferences.getInt(this, default)
    }

    private fun String.editPref(value: Int) {
        sharedPreferences.edit()
            .putInt(this, value)
            .apply()
    }

    var backupDisplayPath: String?
        get() = getPref(backupDisplayPathPref)
        set(value) = editPref(backupDisplayPathPref, value)

    var backupUri: String?
        get() = getPref(backUpLocallyPref)
        set(value) = editPref(backUpLocallyPref, value)

    var version: String
        get() = getPref(versionPref) ?: ""
        set(value) = editPref(versionPref, value)

    var theme: String
        get() = getPref(themePref) ?: "auto"
        set(value) = editPref(versionPref, value)

    var firstLaunch: Boolean
        get() = firstLaunchPref.getPref(true)
        set(value) = firstLaunchPref.editPref(value)

    var preferUseFiles: Boolean
        get() = preferUseFilesPref.getPref(false)
        set(value) = preferUseFilesPref.editPref(value)

    var selectedListIndex: Int
        get() = selectedListPref.getPref(0)
        set(value) {
            _selectedListIndexStateFlow.value = value
            selectedListPref.editPref(value)
        }


    private val _selectedListIndexStateFlow = MutableStateFlow(selectedListIndex)
    val selectedListIndexStateFlow = _selectedListIndexStateFlow.asStateFlow()

    val canAccessBackupUri
        get() = backupUri?.let { DocumentFileCompat.fromUri(app, Uri.parse(it))?.canWrite() } != false

}
