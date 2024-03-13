package com.lolo.io.onelist.core.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun OneListTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        //colorScheme = dynamicLightColorScheme(LocalContext.current),
        colorScheme = lightColorScheme(),
        content = content
    )
}