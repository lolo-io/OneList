package com.lolo.io.onelist.feature.lists.components.core.reorderable_swipeable_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.lolo.io.onelist.core.ui.composables.ComposePreview
import com.lolo.io.onelist.feature.lists.components.core.SwipeState
import kotlin.math.roundToInt

@Composable
fun SwipeableRow(
    modifier: Modifier = Modifier,
    swipeState: SwipeState = SwipeState.NONE,
    setSwipeState: (SwipeState) -> Unit = {},
    backgroundStartToEnd: @Composable() (RowScope.() -> Unit),
    backgroundEndToStart: @Composable() (RowScope.() -> Unit),
    onSwipedToEnd: () -> Unit = {},
    onSwipedToStart: () -> Unit = {},
    onIsSwiping: (Boolean) -> Unit = {},
    content: @Composable() (() -> Unit),

    ) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {

        var offset by remember { mutableFloatStateOf(0f) }
        var targetAnchorState by remember { mutableStateOf(SwipeRowAnchorsState.Default) }

        LaunchedEffect(swipeState) {
            targetAnchorState = when (swipeState) {
                SwipeState.START -> SwipeRowAnchorsState.Start
                SwipeState.END -> SwipeRowAnchorsState.End
                SwipeState.NONE -> SwipeRowAnchorsState.Default
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .swipeableRow(
                    targetAnchorState = targetAnchorState,
                    setTargetAnchorState = {
                        targetAnchorState = it
                    },

                    onOffset = {
                        offset = it
                    },
                    onAnchorState = {
                        when (it) {
                            SwipeRowAnchorsState.Start -> {
                                setSwipeState(SwipeState.START)
                                onSwipedToStart()
                            }

                            SwipeRowAnchorsState.Default -> {
                                setSwipeState(SwipeState.NONE)
                            }

                            SwipeRowAnchorsState.End -> {
                                setSwipeState(SwipeState.END)
                                onSwipedToEnd()
                            }
                        }
                    },
                    onIsSwiping = {
                        onIsSwiping(it)
                    }
                )
        ) {

            // Background
            Row(
                modifier = Modifier
                    .matchParentSize()
            ) {
                if (offset > 0f) {
                    backgroundStartToEnd()
                } else if (offset < 0f) {
                    backgroundEndToStart()
                }
            }

            // Foreground
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
                            color = if (offset == 0f) Color.Transparent else Color.White
                        ) // todo must set a color because otherwise when swiped it is filled by swipe color
                        .fillMaxWidth()

                ) {
                    content()
                }
            }

        }
    }
}

@Preview
@Composable
private fun Preview_SwipeableRow() = ComposePreview {


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
}
