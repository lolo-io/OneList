package com.lolo.io.onelist.feature.lists.components.core.reorderable_swipeable_list

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue.EndToStart
import androidx.compose.material3.SwipeToDismissBoxValue.Settled
import androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import com.lolo.io.onelist.core.designsystem.colors.appColors
import com.lolo.io.onelist.core.designsystem.preview.ThemedPreview
import com.lolo.io.onelist.feature.lists.components.core.SwipeState
import kotlinx.coroutines.launch

interface SwipeableRowScope {
    val swipeState: SwipeState
    val setSwipeState: (SwipeState) -> Unit
}

@Composable
fun swipeableRowScope(
    swipeState: SwipeState,
    setSwipeState: (SwipeState) -> Unit
) =
    object : SwipeableRowScope {
        override val swipeState: SwipeState
            get() = swipeState
        override val setSwipeState: (SwipeState) -> Unit
            get() = setSwipeState
    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableRowScope.SwipeableRow(
    modifier: Modifier = Modifier,
    backgroundStartToEnd: @Composable() (RowScope.() -> Unit),
    backgroundEndToStart: @Composable() (RowScope.() -> Unit),
    onSwipedToEnd: () -> Unit = {},
    onSwipedToStart: () -> Unit = {},
    onSwipedBackToCenter: () -> Unit = {},
    onInitiateSwipeBackToCenter: () -> Unit = {},
    content: @Composable() (() -> Unit),
) {

    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {

        var localSwipeState = remember {
            Settled
        }

        val state = rememberSwipeToDismissBoxState(
            confirmValueChange = {
                when (it) {
                    StartToEnd -> {
                        if (localSwipeState != StartToEnd) {
                            localSwipeState = StartToEnd
                            onSwipedToEnd()
                        }
                        true
                    }

                    EndToStart -> {

                        if (localSwipeState != EndToStart) {
                            localSwipeState = EndToStart
                            onSwipedToStart()
                        }
                        true
                    }

                    Settled -> {
                        if (localSwipeState != Settled) {
                            localSwipeState = Settled
                            onSwipedBackToCenter()
                        }
                        true
                    }
                }
            },
            positionalThreshold = { it * .7f }
        )

        LaunchedEffect(swipeState) {

            when (swipeState) {
                SwipeState.START -> state.snapTo(EndToStart)
                SwipeState.END -> state.snapTo(StartToEnd)
                SwipeState.NONE -> state.reset()
            }
        }

        var hasInitiatedSwipeBackToCenter by remember {
            mutableStateOf(false)
        }

        SwipeToDismissBox(
            state = state, backgroundContent = when (state.dismissDirection) {
                StartToEnd -> backgroundStartToEnd
                EndToStart -> ({
                    Row(modifier = Modifier.pointerInput(Unit) {
                        detectHorizontalDragGestures { _, _ ->
                            if (!hasInitiatedSwipeBackToCenter) {
                                hasInitiatedSwipeBackToCenter = true
                                onInitiateSwipeBackToCenter()

                            }
                            coroutineScope.launch {
                                state.reset()
                            }
                        }
                    }) {
                        backgroundEndToStart()
                    }
                })

                Settled -> ({
                    hasInitiatedSwipeBackToCenter = false
                })
            }
        ) {
            Row(
                Modifier
                    .background(
                        shape = RoundedCornerShape(5f),
                        color = MaterialTheme.appColors.itemRowForeground
                    )
                    .fillMaxWidth()

            ) {
                content()
            }

        }
    }
}

@Preview
@Composable
private fun Preview_SwipeableRow() = ThemedPreview {

    /*
    SwipeableRow(
        backgroundStartToEnd = {
            Box(
                Modifier
                    .background(Color.LightGray)
                    .fillMaxSize()
            ) {}
        },
        backgroundEndToStart = {
            Box(
                Modifier
                    .background(Color.Red)
                    .fillMaxSize()
            ) {}
        },
        content = {
            Text(modifier = Modifier.padding(vertical = 4.dp), text = " Swipe Me")
        },
    )

     */
}

