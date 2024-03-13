package com.lolo.io.onelist.core.design

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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
//    tertiary = ColorLightTokens.Tertiary,
//    onTertiary = ColorLightTokens.OnTertiary,
//    tertiaryContainer = ColorLightTokens.TertiaryContainer,
//    onTertiaryContainer = ColorLightTokens.OnTertiaryContainer,
//    background = ColorLightTokens.Background,
//    onBackground = ColorLightTokens.OnBackground,
//    surface = ColorLightTokens.Surface,
    onSurface = Palette.GRAY,
//    surfaceVariant = ColorLightTokens.SurfaceVariant,
//    onSurfaceVariant = ColorLightTokens.OnSurfaceVariant,
//    surfaceTint = primary,
//    inverseSurface = ColorLightTokens.InverseSurface,
//    inverseOnSurface = ColorLightTokens.InverseOnSurface,
//    error = ColorLightTokens.Error,
//    onError = ColorLightTokens.OnError,
//    errorContainer = ColorLightTokens.ErrorContainer,
//    onErrorContainer = ColorLightTokens.OnErrorContainer,
    outline = Palette.GRAY,
    outlineVariant = Palette.GRAY_LIGHT,
//    scrim = ColorLightTokens.Scrim,
//    surfaceBright = ColorLightTokens.SurfaceBright,
    surfaceContainer = Palette.ONELIST_RED_VERY_LIGHT,
//    surfaceContainerHigh = ColorLightTokens.SurfaceContainerHigh,
//    surfaceContainerHighest = ColorLightTokens.SurfaceContainerHighest,
//    surfaceContainerLow = ColorLightTokens.SurfaceContainerLow,
//    surfaceContainerLowest = Palette.ONELIST_RED_VERY_LIGHT,
//    surfaceDim = ColorLightTokens.SurfaceDim,
)

val ColorScheme.app: AppColors
    @Composable get() = appColors()

data class AppColors(
    val listChipDefaultText: Color = Color.Unspecified,
    val listChipSelectedText: Color = Color.Unspecified,
    val listChipShadowText: Color = Color.Unspecified,
    val listChipDefaultBorder: Color = Color.Unspecified,
    val listSelectedBorder: Color = Color.Unspecified,
    val listChipShadowBorder: Color = Color.Unspecified,
    val listChipDefaultContainer: Color = Color.Unspecified,
    val listChipSelectedContainer: Color = Color.Unspecified,
)

@Composable
fun appColors() = AppColors(
    listChipDefaultText = MaterialTheme.colorScheme.outline,
    listChipSelectedText = MaterialTheme.colorScheme.onPrimary,
    listChipShadowText = MaterialTheme.colorScheme.outlineVariant,
    listChipDefaultBorder = MaterialTheme.colorScheme.outline,
    listSelectedBorder = MaterialTheme.colorScheme.secondary,
    listChipShadowBorder = MaterialTheme.colorScheme.outlineVariant,
    listChipDefaultContainer = Palette.Transparent,
    listChipSelectedContainer = MaterialTheme.colorScheme.secondary,
)

