package com.lolo.io.onelist.feature.lists.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.lolo.io.onelist.core.ui.composables.ComposePreview
import kotlinx.coroutines.launch

interface SwipableRowScope {
    fun reset()
}

private fun swipableRowScope(
    reset: () -> Unit,
) =
    object : SwipableRowScope {
        override fun reset() {
            reset()
        }
    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableRow(
    backgroundStartToEnd: @Composable RowScope.() -> Unit,
    backgroundEndToStart: @Composable RowScope.() -> Unit,
    onSwipedToEnd: () -> Unit = {},
    onSwipedToStart: () -> Unit = {},
    content: @Composable SwipableRowScope.() -> Unit,
) {

    val coroutineScope = rememberCoroutineScope()

    Row(modifier = Modifier.fillMaxWidth()) {
        val state = rememberSwipeToDismissBoxState(
            confirmValueChange = {
                when (it) {
                    SwipeToDismissBoxValue.StartToEnd -> {
                        onSwipedToEnd()
                        true
                    }

                    SwipeToDismissBoxValue.EndToStart -> {
                        onSwipedToStart()
                        true
                    }

                    SwipeToDismissBoxValue.Settled -> true
                }
            }
        )

        val reset = {
            coroutineScope.launch {
                state.reset()
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
                    .background(Color.White)
                    .fillMaxWidth()
            ) {
                content(swipableRowScope(reset = { reset() }))
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
        }
    ) {
        Text(text = "Hello Swiper")
    }
}