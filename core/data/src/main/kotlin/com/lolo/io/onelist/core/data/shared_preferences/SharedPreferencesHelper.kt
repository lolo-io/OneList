package com.lolo.io.onelist.core.data.shared_preferences

import android.app.Application
import android.net.Uri
import android.os.Build
import androidx.preference.PreferenceManager
import com.anggrayudi.storage.file.DocumentFileCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedPreferencesHelper(
    private val app: Application,
) {

    companion object {
        const val VERSION_PREF: String = "version"
        const val SELECTED_LIST_PREF = "selectedList"
        const val BACK_UP_LOCALLY_PREF = "backUpLocally"
        const val BACKUP_DISPLAYED_PATH = "backupDisplayPath" // only for display
        const val THEME_PREF: String = "theme"
        const val FIRST_LAUNCH_PREF = "firstLaunch"
        const val PREFER_USE_FILES_PREF = "preferUseFiles"

        const val THEME_AUTO = "auto"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_DYNAMIC = "dynamic"
    }

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
        get() = getPref(BACKUP_DISPLAYED_PATH)
        set(value) = editPref(BACKUP_DISPLAYED_PATH, value)

    var backupUri: String?
        get() = getPref(BACK_UP_LOCALLY_PREF)
        set(value) = editPref(BACK_UP_LOCALLY_PREF, value)

    var version: String
        get() = getPref(VERSION_PREF) ?: ""
        set(value) = editPref(VERSION_PREF, value)

    var theme: String
        get() = getPref(THEME_PREF) ?: getDefaultTheme()
        set(value) = editPref(VERSION_PREF, value)

    var firstLaunch: Boolean
        get() = FIRST_LAUNCH_PREF.getPref(true)
        set(value) = FIRST_LAUNCH_PREF.editPref(value)

    var preferUseFiles: Boolean
        get() = PREFER_USE_FILES_PREF.getPref(false)
        set(value) = PREFER_USE_FILES_PREF.editPref(value)

    var selectedListIndex: Int
        get() = SELECTED_LIST_PREF.getPref(0)
        set(value) {
            _selectedListIndexStateFlow.value = value
            SELECTED_LIST_PREF.editPref(value)
        }


    private val _selectedListIndexStateFlow = MutableStateFlow(selectedListIndex)
    val selectedListIndexStateFlow = _selectedListIndexStateFlow.asStateFlow()

    val canAccessBackupUri
        get() = backupUri?.let {
            DocumentFileCompat.fromUri(app, Uri.parse(it))?.canWrite()
        } != false


    private fun getDefaultTheme(): String {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
            THEME_LIGHT
        else THEME_AUTO
    }

}
