package com.lolo.io.onelist.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.lolo.io.onelist.feature.lists.navigation.LISTS_SCREEN_ROUTE
import com.lolo.io.onelist.feature.lists.navigation.listsScreen
import com.lolo.io.onelist.feature.settings.navigation.navigateToSettingsScreen
import com.lolo.io.onelist.feature.settings.navigation.settingsScreen
import com.lolo.io.onelist.feature.whatsnew.navigation.navigateToWhatsNewScreen
import com.lolo.io.onelist.feature.whatsnew.navigation.whatsNewScreen

@Composable
fun OneListNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = LISTS_SCREEN_ROUTE
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
        popEnterTransition = { popEnterTransition },
        popExitTransition = { popExitTransition }

    ) {
        listsScreen(
            navigateToSettings = { navController.navigateToSettingsScreen() },
        )

        settingsScreen(
            navigateToWhatsNew = { navController.navigateToWhatsNewScreen() },
        )

        whatsNewScreen(
            onClickContinue = { navController.popBackStack() }
        )
    }
}