package com.lolo.io.onelist.core.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.lolo.io.onelist.core.design.colors.darkColorScheme
import com.lolo.io.onelist.core.design.colors.lightColorScheme

@Composable
fun OneListTheme(content: @Composable () -> Unit) {

    val isDark = isSystemInDarkTheme()
    val colorScheme = if (isDark) darkColorScheme() else lightColorScheme()
    MaterialTheme(
        //colorScheme = dynamicLightColorScheme(LocalContext.current),
        colorScheme = colorScheme,
        content = content,
        typography = typography(colorScheme),
        shapes = Shapes
    )
}