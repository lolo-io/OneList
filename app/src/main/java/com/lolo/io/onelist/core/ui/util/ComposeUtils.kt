package com.lolo.io.onelist.core.ui.util

import androidx.compose.ui.Modifier


fun Modifier.ifThen(condition: Boolean, modifier: Modifier): Modifier {
    if (condition) {
        return this then modifier
    }
    return this
}

fun Modifier.ifNotNullThen(value: Any?, modifier: Modifier): Modifier {
    if (value != null) {
        return this then modifier
    }
    return this
}