package com.lolo.io.onelist.core.designsystem

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.lolo.io.onelist.core.designsystem.colors.darkColorScheme
import com.lolo.io.onelist.core.designsystem.colors.lightColorScheme

@Composable
fun OneListTheme(
    isDynamic: Boolean = false,
    isDark : Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            && isDynamic
        ) {
            if (isDark) dynamicDarkColorScheme(LocalContext.current)
            else dynamicLightColorScheme(LocalContext.current)
        } else
            if (isDark) darkColorScheme() else lightColorScheme()

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
        typography = typography(colorScheme),
        shapes = Shapes
    )
}