package com.lolo.io.onelist.feature.whatsnew.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lolo.io.onelist.feature.whatsnew.WhatsNewScreen

const val WHATS_NEW_SCREEN_ROUTE = "whats_new_screen_route"

fun NavController.navigateToWhatsNewScreen(navOptions: NavOptions? = null) {
    navigate(WHATS_NEW_SCREEN_ROUTE, navOptions)
}

fun NavGraphBuilder.whatsNewScreen(
    onClickContinue : () -> Unit
) {
    composable(
        route = WHATS_NEW_SCREEN_ROUTE
    ) {
        WhatsNewScreen(onClickContinue = onClickContinue)
    }
}