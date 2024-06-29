package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import com.lolo.io.onelist.core.domain.utils.isNotNullOrEmpty

class ShouldShowWhatsNew(private val persistenceHelper: SharedPreferencesHelper) {
    operator fun invoke(currentVersionName: String): Boolean {
        return persistenceHelper.version.isNotNullOrEmpty()
                && persistenceHelper.version.substringBeforeLast(".") !=
                currentVersionName.substringBeforeLast(".")

    }
}