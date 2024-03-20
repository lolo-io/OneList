package com.lolo.io.onelist.feature.lists.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.lolo.io.onelist.core.ui.composables.ComposePreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableRow(
    swipeState: SwipeState,
    backgroundStartToEnd: @Composable() (RowScope.() -> Unit),
    backgroundEndToStart: @Composable() (RowScope.() -> Unit),
    onSwipedToEnd: () -> Unit = {},
    onSwipedToStart: () -> Unit = {},
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable() (() -> Unit),

    ) {
    val corountineScope = rememberCoroutineScope()

    Row(modifier = modifier
        .fillMaxWidth()
        .clickable {
            onClick()
        }) {
        val state = rememberSwipeToDismissBoxState(
            confirmValueChange = {
                when (it) {
                    SwipeToDismissBoxValue.StartToEnd -> {
                        onSwipedToEnd()
                        true
                    }

                    SwipeToDismissBoxValue.EndToStart -> {
                        corountineScope.launch {
                            delay(500)
                            onSwipedToStart()
                        }
                        true
                    }

                    SwipeToDismissBoxValue.Settled -> true
                }
            }
        )


        LaunchedEffect(swipeState) {
            when (swipeState) {
                SwipeState.START -> state.snapTo(SwipeToDismissBoxValue.EndToStart)
                SwipeState.END -> state.snapTo(SwipeToDismissBoxValue.StartToEnd)
                SwipeState.NONE -> state.reset()
            }
        }


        SwipeToDismissBox(
            state = state, backgroundContent = when (state.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> backgroundStartToEnd
                SwipeToDismissBoxValue.EndToStart -> backgroundEndToStart
                SwipeToDismissBoxValue.Settled -> transparentBackground
            }
        ) {
            Row(
                Modifier
                    .background(shape = RoundedCornerShape(5f), color = Color.White) // todo must set a color becaus otherwise when swiped it is filled by swipe color
                    .fillMaxWidth()

            ) {
                content()
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

    val swipeState by remember {
        mutableStateOf(SwipeState.NONE)
    }

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
        swipeState = swipeState
    )
}