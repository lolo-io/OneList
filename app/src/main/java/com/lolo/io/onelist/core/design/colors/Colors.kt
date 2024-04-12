package com.lolo.io.onelist.core.design.colors

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.lolo.io.onelist.core.design.Palette

data class AppColors(

    // Header
    val oneListTopLogo: Color,
    val settingsIcon: Color,
    val shareListIcon: Color,
    val addListIcon: Color,
    val editListIcon: Color,
    val deleteListIcon: Color,

    // Chips
    val listChipDefaultText: Color,
    val listChipSelectedText: Color,
    val listChipShadowText: Color,
    val listChipDefaultBorder: Color,
    val listSelectedBorder: Color,
    val listChipShadowBorder: Color,
    val listChipDefaultContainer: Color,
    val listChipSelectedContainer: Color,

    // Items
    val itemBullet: Color,
    val itemComment: Color,
    val itemDone: Color,
    val swipeDeleteBackground: Color,
    val swipeEditBackground: Color,
    val itemArrow: Color,
    val itemRowForeground: Color,

    // Add Item
    val addItemCheck: Color,
    val addItemCommentArrow: Color,

    // Dialogs
    val dialogBackgroud: Color,
    val dialogBorder: Color,
    val dialogDeleteWarning: Color,
    val dialogDeleteCheckBoxRipple: Color,
    val dialogDeleteJutsClearList: Color,
    val dialogDeleteDeleteFile: Color,
    val dialogButtonPrimary: Color,
    val dialogButtonCancel: Color,

    // Whats New
    val whatsNewTitle: Color,
    val whatsNewItemIcon: Color,

    // TextField
    val textFieldBorder: Color,
    val textFieldCursor: Color,
    val textFieldText: Color,
    val textFieldPlaceholder: Color,
    val textFieldColors: TextFieldColors,
)

val MaterialTheme.appColors: AppColors
    @Composable get() = appColors()

@Composable
fun appColors(
    isDark: Boolean = isSystemInDarkTheme()
) = AppColors(

    // Header
    oneListTopLogo = if (isDark) MaterialTheme.colorScheme.onSurface
    else MaterialTheme.colorScheme.primary,
    settingsIcon = MaterialTheme.colorScheme.outline,
    shareListIcon = MaterialTheme.colorScheme.onBackground,
    addListIcon = MaterialTheme.colorScheme.onBackground,
    editListIcon = MaterialTheme.colorScheme.outline,
    deleteListIcon = MaterialTheme.colorScheme.primary,

    // Dialogs
    dialogBackgroud = MaterialTheme.colorScheme.surface,
    dialogBorder = MaterialTheme.colorScheme.secondary,
    dialogDeleteWarning = MaterialTheme.colorScheme.error,
    dialogDeleteCheckBoxRipple = MaterialTheme.colorScheme.primary,
    dialogDeleteJutsClearList = MaterialTheme.colorScheme.outline,
    dialogDeleteDeleteFile = MaterialTheme.colorScheme.outline,
    dialogButtonPrimary = MaterialTheme.colorScheme.primary,
    dialogButtonCancel = MaterialTheme.colorScheme.outline,

    // Chips
    listChipDefaultText = MaterialTheme.colorScheme.outline,
    listChipSelectedText = MaterialTheme.colorScheme.onPrimary,
    listChipShadowText = MaterialTheme.colorScheme.outlineVariant,
    listChipDefaultBorder = MaterialTheme.colorScheme.outline,
    listSelectedBorder = MaterialTheme.colorScheme.secondary,
    listChipShadowBorder = MaterialTheme.colorScheme.outlineVariant,
    listChipDefaultContainer = Palette.Transparent,
    listChipSelectedContainer = MaterialTheme.colorScheme.secondary,

    // Items
    itemRowForeground = MaterialTheme.colorScheme.surface,
    itemComment = MaterialTheme.colorScheme.outline,
    itemBullet = MaterialTheme.colorScheme.primary,
    itemDone = MaterialTheme.colorScheme.outline,
    itemArrow = MaterialTheme.colorScheme.onSurface,
    swipeDeleteBackground = MaterialTheme.colorScheme.secondary,
    swipeEditBackground = MaterialTheme.colorScheme.outlineVariant,

    // Add Item
    addItemCheck = MaterialTheme.colorScheme.primary,
    addItemCommentArrow = MaterialTheme.colorScheme.outline,

    // Whats New
    whatsNewTitle = MaterialTheme.colorScheme.tertiary,
    whatsNewItemIcon = MaterialTheme.colorScheme.primary,

    // TextField
    textFieldBorder = MaterialTheme.colorScheme.outline,
    textFieldCursor = MaterialTheme.colorScheme.outline,
    textFieldText = MaterialTheme.colorScheme.onSurface,
    textFieldPlaceholder = MaterialTheme.colorScheme.outline,
    textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.background,
        unfocusedContainerColor = MaterialTheme.colorScheme.background,
        cursorColor = MaterialTheme.colorScheme.onBackground,
        focusedLeadingIconColor = MaterialTheme.colorScheme.outline,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.outline,
        focusedTrailingIconColor = MaterialTheme.colorScheme.outline,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.outline,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        selectionColors = TextSelectionColors(
            handleColor = MaterialTheme.colorScheme.secondary,
            backgroundColor = Color.Transparent
        ),
    ),
)