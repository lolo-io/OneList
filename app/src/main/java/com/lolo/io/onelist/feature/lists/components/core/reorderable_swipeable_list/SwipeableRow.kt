package com.lolo.io.onelist.feature.lists.components.core.reorderable_swipeable_list

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import com.lolo.io.onelist.core.design.colors.appColors
import com.lolo.io.onelist.core.ui.composables.ComposePreview
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
    content: @Composable() (() -> Unit),
) {

    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {

        val state = rememberSwipeToDismissBoxState(
            confirmValueChange = {
                when (it) {
                    StartToEnd -> {
                        onSwipedToEnd()
                        true
                    }

                    EndToStart -> {
                        onSwipedToStart()
                        true
                    }

                    Settled -> {
                        onSwipedBackToCenter()
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

        SwipeToDismissBox(
            state = state, backgroundContent = when (state.dismissDirection) {
                StartToEnd -> backgroundStartToEnd
                EndToStart -> ({
                    Row(modifier = Modifier.pointerInput(Unit) {
                        detectHorizontalDragGestures { change, dragAmount ->
                            coroutineScope.launch {
                                state.reset()
                            }
                        }
                    }) {
                        backgroundEndToStart()
                    }
                })

                Settled -> ({})
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
private fun Preview_SwipeableRow() = ComposePreview {

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

