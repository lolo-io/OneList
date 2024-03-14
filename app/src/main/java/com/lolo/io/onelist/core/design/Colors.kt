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
    onBackground = Palette.PURE_BLACK,
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



data class AppColors(
    val listChipDefaultText: Color,
    val listChipSelectedText: Color,
    val listChipShadowText: Color,
    val listChipDefaultBorder: Color,
    val listSelectedBorder: Color,
    val listChipShadowBorder: Color,
    val listChipDefaultContainer: Color,
    val listChipSelectedContainer: Color,
    val itemBullet: Color,
    val itemComment: Color,
    val itemDone: Color,

    val swipeDeleteBackground: Color,
    val swipeEditBackground: Color
)


val ColorScheme.app: AppColors
    @Composable get() = appColors()

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
    itemComment = MaterialTheme.colorScheme.outline,
    itemBullet = MaterialTheme.colorScheme.primary,
    itemDone = MaterialTheme.colorScheme.outline,
    swipeDeleteBackground = MaterialTheme.colorScheme.secondary,
    swipeEditBackground = MaterialTheme.colorScheme.outlineVariant,
)

