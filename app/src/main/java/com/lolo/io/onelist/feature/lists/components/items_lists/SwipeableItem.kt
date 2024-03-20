package com.lolo.io.onelist.feature.lists.components.items_lists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.lolo.io.onelist.core.design.Palette
import com.lolo.io.onelist.core.design.app
import com.lolo.io.onelist.core.design.space
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.preview
import com.lolo.io.onelist.core.ui.composables.ComposePreview
import com.lolo.io.onelist.feature.lists.components.core.SwipeState
import com.lolo.io.onelist.feature.lists.components.core.SwipeableRow
import com.lolo.io.onelist.feature.lists.components.core.SwipeableRowScope
import com.lolo.io.onelist.feature.lists.components.core.swipeableRowScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun SwipeableRowScope.SwipeableItem(
    item: Item,
    modifier: Modifier = Modifier,
    onShowOrHideComment : () -> Unit = {},
    onSwipedToStart: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    val onSwipedToEnd = {
        // param.onSwiped()
    }

    val onSwipedToStart = {
        onSwipedToStart()
    }



    SwipeableRow(
        modifier = modifier,
        swipeState = swipeState,
        onClick = onClick,
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
            setSwipeState(SwipeState.END)
            onSwipedToEnd()
        },
        onSwipedToStart = {
            setSwipeState(SwipeState.START)
            onSwipedToStart()
        }
    ) {

        ItemRow(item,
            onClickDisplayComment = {
                onShowOrHideComment()
            })
    }
}

@Preview
@Composable
private fun Preview_SwipeableItem() = ComposePreview {

    val coroutineScope = rememberCoroutineScope()

    var swipeState by remember {
        mutableStateOf(SwipeState.NONE)
    }


    val PreviewSwipeableItem: @Composable SwipeableRowScope.() -> Unit = {
        SwipeableItem(Item.preview)
    }

    Column {
        val scope = swipeableRowScope(
            swipeState = swipeState,
            setSwipeState = {
                swipeState = it
                coroutineScope.launch {
                    delay(1000)
                    swipeState = SwipeState.NONE
                }
            }
        )
        PreviewSwipeableItem(scope)
        Button(
            modifier = Modifier.padding(MaterialTheme.space.Normal),
            onClick = {
                scope.setSwipeState(SwipeState.NONE) }
        ) {
            Text(text = "Reset Swipe")
        }
    }

}