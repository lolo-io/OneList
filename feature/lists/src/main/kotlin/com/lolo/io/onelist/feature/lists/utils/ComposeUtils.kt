package com.lolo.io.onelist.feature.lists.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Modifier.ifThen(condition: Boolean, modifier: @Composable Modifier.() -> Modifier): Modifier {
    if (condition) {
        return this then modifier()
    }
    return this
}

@Composable
fun Modifier.ifNotNullThen(value: Any?, modifier: @Composable Modifier.() -> Modifier): Modifier {
    if (value != null) {
        return this then modifier()
    }
    return this
}