package com.lolo.io.onelist.feature.lists.components.core

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitDragOrCancellation
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue


enum class SwipeRowAnchorsState {
    Start, Default, End;

    fun anchor(anchors: SwipeRowAnchors): Float {
        return when (this) {
            Start -> anchors.start
            Default -> anchors.default
            End -> anchors.end
        }
    }

    fun previous(): SwipeRowAnchorsState {
        return when (this) {
            Start -> Start
            Default -> Start
            End -> Default
        }
    }

    fun next(): SwipeRowAnchorsState {
        return when (this) {
            Start -> Default
            Default -> End
            End -> End
        }
    }
}

data class SwipeRowAnchors(
    val start: Float = 0f,
    val default: Float = 0f,
    val end: Float = 0f
)

@Composable
fun Modifier.swipeableRow(
    anchors: SwipeRowAnchors,
    onOffset: (offset: Float) -> Unit = {},
    onState: (state: SwipeRowAnchorsState) -> Unit = {},
    onIsSwiping: (Boolean) -> Unit = {}
): Modifier {

    var offset by remember { mutableFloatStateOf(0f) }
    var pointerDownX by remember { mutableStateOf(0f) }
    var previousPointerPositionX by remember { mutableFloatStateOf(0f) }

    var isFling by remember { mutableStateOf(false) }

    val animateOffset = animateFloatAsState(
        targetValue = offset,
        animationSpec = if(!isFling) spring() else  tween(650),
        label = "Offset animation"
    )


    var anchorState by remember {
        mutableStateOf(SwipeRowAnchorsState.Default)
    }


    LaunchedEffect(offset) {
        val isSwiping = if (offset != 0f) true else {
            delay(200)
            false
        }

        onIsSwiping(isSwiping)
    }

    LaunchedEffect(animateOffset.value) {
        onOffset(animateOffset.value)
    }

    LaunchedEffect(anchorState) {
        onState(anchorState)
    }

    return this then Modifier
        .pointerInput(anchors) {
            awaitPointerEventScope {
                while (true) {
                    awaitPointerEvent().changes.forEach {
                        pointerDownX = it.position.x

                        var isHorizontal = false


                        awaitTouchSlopOrCancellation(it.id) { change, overSlop ->

                            if (overSlop.x.absoluteValue < overSlop.y.absoluteValue * 3) {
                                isHorizontal = false
                            } else {
                                isHorizontal = true

                                if (!isFling) {
                                    offset =
                                        change.position.x - pointerDownX + anchorState.anchor(
                                            anchors
                                        )
                                }

                                val deltaX = change.position.x - change.previousPosition.x
                                val deltaTime = change.uptimeMillis - change.previousUptimeMillis
                                val velocity = deltaX / deltaTime

                                if (!isFling && previousPointerPositionX != 0f && velocity >= 3.4) {
                                    isFling = true
                                    anchorState = anchorState.next()
                                    offset = anchorState.anchor(anchors)

                                }
                                if (!isFling && previousPointerPositionX != 0f && velocity <= -3.4) {
                                    isFling = true
                                    anchorState = anchorState.previous()
                                    offset = anchorState.anchor(anchors)
                                }
                                previousPointerPositionX = change.position.x

                            }
                        }

                        previousPointerPositionX = 0f

                        if (isHorizontal && !isFling) {
                            horizontalDrag(it.id) {
                                offset =
                                    it.position.x - pointerDownX + anchorState.anchor(anchors)
                            }
                        }

                        awaitDragOrCancellation(it.id)
                        anchorState = if (offset >= anchors.end * 0.5f) {
                            SwipeRowAnchorsState.End
                        } else if (offset <= anchors.start * 0.6f) {
                            SwipeRowAnchorsState.Start
                        } else {
                            SwipeRowAnchorsState.Default

                        }
                        offset = anchorState.anchor(anchors)

                        isFling = false
                    }
                }
            }
        }
}