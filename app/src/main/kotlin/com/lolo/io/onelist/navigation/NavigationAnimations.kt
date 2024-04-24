package com.lolo.io.onelist.navigation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.ui.unit.IntOffset

private const val NAVIGATION_ANIMATION_DURATION = 250

internal val enterTransition =
    slideIn(
        animationSpec = tween(
            NAVIGATION_ANIMATION_DURATION, easing = LinearEasing
        ),
        initialOffset = { IntOffset(it.width, 0) }

    )


internal val exitTransition =
    scaleOut(
        animationSpec = tween(
            NAVIGATION_ANIMATION_DURATION, easing = LinearEasing
        ),
        targetScale = 0.9f
    )

internal val popEnterTransition =
    scaleIn(
        animationSpec = tween(
            NAVIGATION_ANIMATION_DURATION, easing = LinearEasing
        ),
        initialScale = 0.9f
    )


internal val popExitTransition =
    slideOut(
        animationSpec = tween(
            NAVIGATION_ANIMATION_DURATION, easing = LinearEasing
        ),
        targetOffset = { IntOffset(it.width, 0) }
    )
