package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.BuildConfig
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper

class ShowWhatsNew(private val persistenceHelper: SharedPreferencesHelper) {

    operator fun invoke(): Boolean {
        return persistenceHelper.version.substringBeforeLast(".") !=
                BuildConfig.VERSION_NAME.substringBeforeLast(".")

    }
}