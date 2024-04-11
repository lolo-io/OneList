package com.lolo.io.onelist.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lolo.io.onelist.feature.settings.SettingsScreen

const val SETTINGS_SCREEN_ROUTE = "settings_screen_route"

fun NavController.navigateToSettingsScreen(navOptions: NavOptions? = null) {
    navigate(SETTINGS_SCREEN_ROUTE, navOptions)
}

fun NavGraphBuilder.settingsScreen(navigateToWhatsNew: () -> Unit) {
    composable(
        route = SETTINGS_SCREEN_ROUTE
    ) {
        SettingsScreen(navigateToWhatsNew = navigateToWhatsNew)
    }
}