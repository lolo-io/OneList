package com.lolo.io.onelist.core.data.utils

import androidx.annotation.StringRes

sealed class UIString(
    val resId: Int,
    val restResIds: List<Int> = listOf()
) {

    class StringResource(
        @StringRes resId: Int
    ): UIString(
        resId
    )

    class StringResources(
        @StringRes vararg resIds: Int
    ): UIString(
        resId = resIds.getOrNull(0) ?: 0,
        restResIds = resIds.drop(1)
    )
}