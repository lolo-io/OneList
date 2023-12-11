package com.lolo.io.onelist.core.ui.util

import android.content.Context
import androidx.annotation.StringRes

sealed class AppStrings {
    class StringResource(
        @StringRes val resId: Int
    ): AppStrings()
}




fun Context.getAppString(resId: Int): String {
    return getString(resId)
}