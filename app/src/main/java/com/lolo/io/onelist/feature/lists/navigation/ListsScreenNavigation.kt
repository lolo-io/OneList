package com.lolo.io.onelist.feature.lists.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.lolo.io.onelist.feature.lists.ListsScreen
import com.lolo.io.onelist.feature.whatsnew.navigation.navigateToWhatsNewScreen

const val LISTS_SCREEN_ROUTE = "lists_screen_route"

fun NavGraphBuilder.listsScreen(
    navigateToSettings: () -> Unit) {
    composable(
        route = LISTS_SCREEN_ROUTE
    ) {
        ListsScreen(navigateToSettings = navigateToSettings)
    }
}