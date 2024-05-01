package com.lolo.io.onelist.core.data.shared_preferences

import android.app.Application
import android.net.Uri
import android.os.Build
import androidx.preference.PreferenceManager
import com.anggrayudi.storage.file.DocumentFileCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
class SharedPreferencesHelperImpl(
    private val app: Application,
) : SharedPreferencesHelper {

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

    override var backupDisplayPath: String?
        get() = getPref(SharedPreferencesHelper.BACKUP_DISPLAYED_PATH)
        set(value) = editPref(SharedPreferencesHelper.BACKUP_DISPLAYED_PATH, value)

    override var backupUri: String?
        get() = getPref(SharedPreferencesHelper.BACK_UP_LOCALLY_PREF)
        set(value) = editPref(SharedPreferencesHelper.BACK_UP_LOCALLY_PREF, value)

    override var version: String
        get() = getPref(SharedPreferencesHelper.VERSION_PREF) ?: ""
        set(value) = editPref(SharedPreferencesHelper.VERSION_PREF, value)

    override var theme: String
        get() = getPref(SharedPreferencesHelper.THEME_PREF) ?: getDefaultTheme()
        set(value) = editPref(SharedPreferencesHelper.THEME_PREF, value)

    override var firstLaunch: Boolean
        get() = SharedPreferencesHelper.FIRST_LAUNCH_PREF.getPref(true)
        set(value) = SharedPreferencesHelper.FIRST_LAUNCH_PREF.editPref(value)

    override var preferUseFiles: Boolean
        get() = SharedPreferencesHelper.PREFER_USE_FILES_PREF.getPref(false)
        set(value) = SharedPreferencesHelper.PREFER_USE_FILES_PREF.editPref(value)

    override var selectedListIndex: Int
        get() = SharedPreferencesHelper.SELECTED_LIST_PREF.getPref(0)
        set(value) {
            _selectedListIndexStateFlow.value = value
            SharedPreferencesHelper.SELECTED_LIST_PREF.editPref(value)
        }


    private val _selectedListIndexStateFlow = MutableStateFlow(selectedListIndex)
    override val selectedListIndexStateFlow = _selectedListIndexStateFlow.asStateFlow()

    override val canAccessBackupUri
        get() = backupUri?.let {
            DocumentFileCompat.fromUri(app, Uri.parse(it))?.canWrite()
        } != false


    private fun getDefaultTheme(): String {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
            SharedPreferencesHelper.THEME_LIGHT
        else SharedPreferencesHelper.THEME_AUTO
    }

}
