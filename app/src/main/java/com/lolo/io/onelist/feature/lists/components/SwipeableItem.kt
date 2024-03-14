package com.lolo.io.onelist.feature.lists.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.lolo.io.onelist.core.design.Palette
import com.lolo.io.onelist.core.design.app
import com.lolo.io.onelist.core.design.space
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.preview
import com.lolo.io.onelist.core.ui.composables.ComposePreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.lolo.io.onelist.feature.lists.components.core.SwipeableRow

enum class SwipeState {
    START, END, NONE
}

@Composable
internal fun SwipeableItem(
    item: Item,
) {
    var swipeState by remember {
        mutableStateOf<SwipeState>(SwipeState.NONE)
    }

    val onSwipedToEnd = {
        // param.onSwiped()
        swipeState = SwipeState.END

        GlobalScope.launch {
            delay(1000)
            swipeState = SwipeState.NONE
        }
    }

    val onSwipedToStart = {
        // param.onSwiped()
        swipeState = SwipeState.START

        GlobalScope.launch {
            delay(1000)
            swipeState = SwipeState.NONE
        }
    }

    SwipeableRow(
        backgroundStartToEnd = {
            Box(
                Modifier
                    .background(MaterialTheme.colorScheme.app.swipeEditBackground)
                    .fillMaxSize()
                    .padding(start = MaterialTheme.space.Normal),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    tint = Palette.PURE_WHITE,
                    contentDescription = null
                )
            }
        },
        backgroundEndToStart = {
            Box(
                Modifier
                    .background(MaterialTheme.colorScheme.app.swipeDeleteBackground)
                    .padding(end = MaterialTheme.space.Normal)
                    .fillMaxSize(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    tint = Palette.PURE_WHITE,
                    contentDescription = null
                )
            }
        },
        onSwipedToEnd = {
            onSwipedToEnd()
        },
        onSwipedToStart = {
            onSwipedToStart()
        }
    ) {

        LaunchedEffect(swipeState) {
            if (swipeState == SwipeState.NONE) {
                reset()
            }
        }

        ItemRow(item)
    }
}


@Preview
@Composable
private fun Preview_SwipeableItem() = ComposePreview {
    SwipeableItem(Item.preview)
}