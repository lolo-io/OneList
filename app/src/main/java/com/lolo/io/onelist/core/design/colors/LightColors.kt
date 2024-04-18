package com.lolo.io.onelist.core.design.colors

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.lolo.io.onelist.core.design.Palette
import androidx.compose.material3.lightColorScheme as materialLightColorScheme

@Composable
fun lightColorScheme(): ColorScheme = materialLightColorScheme(
    primary = Palette.ONELIST_RED,
    onPrimary = Palette.PURE_WHITE,
//    primaryContainer = ColorLightTokens.PrimaryContainer,
//    onPrimaryContainer = ColorLightTokens.OnPrimaryContainer,
//    inversePrimary = ColorLightTokens.InversePrimary,
    secondary = Palette.ONELIST_RED_LIGHT,
//    onSecondary = ColorLightTokens.OnSecondary,
//    secondaryContainer = ColorLightTokens.SecondaryContainer,
//    onSecondaryContainer = ColorLightTokens.OnSecondaryContainer,
    tertiary = Palette.ONELIST_RED_DARK,
//    onTertiary = ColorLightTokens.OnTertiary,
//    tertiaryContainer = ColorLightTokens.TertiaryContainer,
//    onTertiaryContainer = ColorLightTokens.OnTertiaryContainer,
    background = Palette.PURE_WHITE,
    onBackground = Palette.PURE_BLACK,
//    surface = ColorLightTokens.Surface,
//    onSurface = Palette.GRAY,
//    surfaceVariant = ColorLightTokens.SurfaceVariant,
//    onSurfaceVariant = ColorLightTokens.OnSurfaceVariant,
//    surfaceTint = primary,
//    inverseSurface = ColorLightTokens.InverseSurface,
//    inverseOnSurface = ColorLightTokens.InverseOnSurface,
    error = Palette.ONELIST_RED_DARK,
//    onError = ColorLightTokens.OnError,
//    errorContainer = ColorLightTokens.ErrorContainer,
//    onErrorContainer = ColorLightTokens.OnErrorContainer,
    outline = Palette.GRAY,
    outlineVariant = Palette.GRAY_LIGHT,
//    scrim = ColorLightTokens.Scrim,
//    surfaceBright = ColorLightTokens.SurfaceBright,
//    surfaceContainer = Color.Yellow,
//    surfaceContainerHigh = ColorLightTokens.SurfaceContainerHigh,
//    surfaceContainerHighest = ColorLightTokens.SurfaceContainerHighest,
//    surfaceContainerLow = ColorLightTokens.SurfaceContainerLow,
//    surfaceContainerLowest = Palette.ONELIST_RED_VERY_LIGHT,
//    surfaceDim = ColorLightTokens.SurfaceDim,
)
