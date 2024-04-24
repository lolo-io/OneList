package com.lolo.io.onelist.core.domain.use_cases

//import com.lolo.io.onelist.BuildConfig
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import isNotNullOrEmpty

class ShowWhatsNew(private val persistenceHelper: SharedPreferencesHelper) {

    operator fun invoke(): Boolean {
        return false
        // TODO Remove BuildConfig
       /* return persistenceHelper.version.isNotNullOrEmpty() && persistenceHelper.version.substringBeforeLast(".") !=
                BuildConfig.VERSION_NAME.substringBeforeLast(".")

        */
    }
}