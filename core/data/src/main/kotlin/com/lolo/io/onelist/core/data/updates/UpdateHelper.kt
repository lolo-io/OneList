package com.lolo.io.onelist.core.data.updates

import androidx.fragment.app.FragmentActivity
import com.lolo.io.onelist.core.data.repository.OneListRepository
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper

class UpdateHelper(
    private val preferences: SharedPreferencesHelper,
    private val repository: OneListRepository
) {
    fun applyMigrationsIfNecessary(
        previousVersionName: String,
        currentVersionName: String,
        activity: FragmentActivity,
        then: () -> Unit
    ) {

        val previousVersion = Version(previousVersionName)
        val currentVersion = Version(currentVersionName)

        if (previousVersionName.isEmpty() || previousVersion == currentVersion) {
            then()
        } else when {
            previousVersion.minor < 4 ->
                UpdateFromBelowOneDotFour(preferences, repository).update(activity, then)

            previousVersionName.startsWith("1.4") &&
                    previousVersion.patch < 2 -> UpdateFromOneDotFourDotOne(repository).update(then)

            else -> then()
        }
    }

}