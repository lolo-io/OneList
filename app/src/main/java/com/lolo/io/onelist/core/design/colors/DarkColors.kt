package com.lolo.io.onelist.core.design.colors

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.lolo.io.onelist.core.design.Palette
import androidx.compose.material3.darkColorScheme as materialDarkColorScheme

@Composable
fun darkColorScheme(): ColorScheme = materialDarkColorScheme(
    primary = Palette.ONELIST_RED_LIGHT,
    onPrimary = Palette.PURE_WHITE,
//    primaryContainer = ColorLightTokens.PrimaryContainer,
//    onPrimaryContainer = ColorLightTokens.OnPrimaryContainer,
//    inversePrimary = ColorLightTokens.InversePrimary,
    secondary = Palette.ONELIST_RED_LIGHT,
//    onSecondary = ColorLightTokens.OnSecondary,
//    secondaryContainer = ColorLightTokens.SecondaryContainer,
//    onSecondaryContainer = ColorLightTokens.OnSecondaryContainer,
    tertiary = Palette.ONELIST_RED,
//    onTertiary = ColorLightTokens.OnTertiary,
//    tertiaryContainer = ColorLightTokens.TertiaryContainer,
//    onTertiaryContainer = ColorLightTokens.OnTertiaryContainer,
    background = Palette.PURE_BLACK,
    //   onBackground = Palette.PURE_WHITE,
//    surface = Palette.PURE_BLACK,
//    onSurface = Palette.GRAY_LIGHT,
//    surfaceVariant = ColorLightTokens.SurfaceVariant,
//    onSurfaceVariant = ColorLightTokens.OnSurfaceVariant,
//    surfaceTint = primary,
//    inverseSurface = ColorLightTokens.InverseSurface,
//    inverseOnSurface = ColorLightTokens.InverseOnSurface,
    error = Palette.ONELIST_RED_LIGHT,
//    onError = ColorLightTokens.OnError,
//    errorContainer = ColorLightTokens.ErrorContainer,
//    onErrorContainer = ColorLightTokens.OnErrorContainer,
    outline = Palette.GRAY,
    outlineVariant = Palette.GRAY_DARK,
//    scrim = ColorLightTokens.Scrim,
//    surfaceBright = ColorLightTokens.SurfaceBright,
    surfaceContainer = Color.Yellow,
//    surfaceContainerHigh = ColorLightTokens.SurfaceContainerHigh,
//    surfaceContainerHighest = ColorLightTokens.SurfaceContainerHighest,
//    surfaceContainerLow = ColorLightTokens.SurfaceContainerLow,
//    surfaceContainerLowest = Palette.ONELIST_RED_VERY_LIGHT,
//    surfaceDim = ColorLightTokens.SurfaceDim,
)
