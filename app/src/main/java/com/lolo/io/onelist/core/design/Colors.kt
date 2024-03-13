package com.lolo.io.onelist.core.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme as materialLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun lightColorScheme() : ColorScheme = materialLightColorScheme(
    primary = Palette.ONELIST_RED,
)




val ColorScheme.ext: ExtensionColors
    @Composable get() {
        val dark = isSystemInDarkTheme()
        return if (dark) LightExtensionsColors else LightExtensionsColors
    }

abstract class ExtensionColors {
    abstract val listChipDraggingBackground: Color
    abstract val listChipDefaultText: Color
    abstract val listChipDefaultBorder: Color
}

object LightExtensionsColors: ExtensionColors() {
    override val listChipDraggingBackground = Palette.GRAY_LIGHT
    override val listChipDefaultText = Palette.GRAY
    override val listChipDefaultBorder = Palette.GRAY
}
