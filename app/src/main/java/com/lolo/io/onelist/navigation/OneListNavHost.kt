package com.lolo.io.onelist.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
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
    startDestination: String = LISTS_SCREEN_ROUTE
) {

    val navController = rememberNavController()
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