package com.lolo.io.onelist.feature.lists.components.core

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import com.lolo.io.onelist.core.ui.composables.ComposePreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


interface SwipeableRowScope {
    val swipeState: SwipeState
    val setSwipeState: (SwipeState) -> Unit
}

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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeableRow(
    modifier: Modifier = Modifier,
    backgroundStartToEnd: @Composable() (RowScope.() -> Unit),
    backgroundEndToStart: @Composable() (RowScope.() -> Unit),
    onSwipedToEnd: () -> Unit = {},
    onSwipedToStart: () -> Unit = {},
    onClick: () -> Unit = {},
    onIsSwiping: (Boolean) -> Unit = {},
    content: @Composable() (() -> Unit),

    ) {
    val corountineScope = rememberCoroutineScope()

    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {

        var offset by remember { mutableFloatStateOf(0f) }
        var start by remember { mutableFloatStateOf(0f) }
        var end by remember { mutableFloatStateOf(0f) }
        var isSwiping by remember { mutableStateOf(false) }

        var swipeLeftJob by remember { mutableStateOf<Job?>(null) }

        Box(
            modifier = Modifier
                .onSizeChanged {
                    start = -it.width.toFloat()
                    end = it.width.toFloat()
                }
                .fillMaxWidth()
                .wrapContentHeight()
                .swipeableRow(
                    anchors = SwipeRowAnchors(
                        start, 0f, end
                    ),
                    onOffset = {
                        offset = it
                    },
                    onState = {
                        when (it) {
                            SwipeRowAnchorsState.Start -> {
                                swipeLeftJob = corountineScope.launch {
                                    delay(2250)
                                    onSwipedToStart()
                                }
                            }

                            SwipeRowAnchorsState.Default -> {
                                swipeLeftJob?.cancel()
                            }

                            SwipeRowAnchorsState.End -> {
                                onSwipedToEnd()
                            }
                        }
                    },
                    onIsSwiping = {
                        isSwiping = it
                        onIsSwiping(it)
                    }
                )
                .combinedClickable(
                    onLongClick = {}
                ) {
                    corountineScope.launch {
                        if (!isSwiping) {
                            onClick()
                        }
                    }
                }
        ) {

            // BG
            Row(
                modifier = Modifier
                    .matchParentSize()
            ) {
                if (offset > 0f) {
                    backgroundStartToEnd()
                } else {
                    backgroundEndToStart()
                }
            }

            // Front
            Box(
                Modifier
                    .offset {
                        IntOffset(
                            x = offset.roundToInt(), y = 0
                        )
                    }) {
                Row(
                    Modifier
                        .background(
                            shape = RoundedCornerShape(5f),
                            color = Color.White
                        ) // todo must set a color becaus otherwise when swiped it is filled by swipe color
                        .fillMaxWidth()

                ) {
                    content()
                }
            }

        }
    }
}

private val transparentBackground: @Composable RowScope.() -> Unit = {
    Row(
        Modifier
            .background(Color.Transparent)
            .fillMaxSize()
    ) {}
}


@Preview
@Composable
private fun Preview_SwipableRow() = ComposePreview {


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
            Text(text = "Hello Swiper")
        },
    )
}
