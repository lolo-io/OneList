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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.lolo.io.onelist.core.design.Palette
import com.lolo.io.onelist.core.design.colors.appColors
import com.lolo.io.onelist.core.design.space
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.preview
import com.lolo.io.onelist.core.ui.composables.ComposePreview
import com.lolo.io.onelist.feature.lists.components.core.SwipeState
import com.lolo.io.onelist.feature.lists.components.core.reorderable_swipeable_list.SwipeableRow

@Composable
internal fun SwipeableItem(
    item: Item,
    modifier: Modifier = Modifier,
    swipeState: SwipeState = SwipeState.NONE,
    setSwipeState: (SwipeState) -> Unit = {},
    onSwipedToStart: () -> Unit = {},
    onSwipedToEnd: () -> Unit = {},
    onSwipedBackToCenter: () -> Unit = {},
    onIsSwiping: (Boolean) -> Unit = {},
    drawItem: @Composable (Item) -> Unit = {}
) {

    SwipeableRow(
        modifier = modifier,
        onIsSwiping = onIsSwiping,
        swipeState = swipeState,
        setSwipeState = setSwipeState,
        backgroundStartToEnd = {
            Box(
                Modifier
                    .background(MaterialTheme.appColors.swipeEditBackground)
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
                    .background(MaterialTheme.appColors.swipeDeleteBackground)
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
        },
        onSwipedBackToCenter= {
            onSwipedBackToCenter()
        }
    ) {
        drawItem(item)
    }
}

@Preview
@Composable
private fun Preview_SwipeableItem() = ComposePreview {

    var swipeState by remember {
        mutableStateOf(SwipeState.NONE)
    }

    Column {
            SwipeableItem(
                Item.preview,
                setSwipeState = {
                    swipeState = it
                },
                drawItem = {
                    ItemUI(Item.preview,
                        onClickDisplayComment = {
                            showPreviewDialog("onClickDisplayComment")
                        }
                    )
                },
            )
            Button(
                modifier = Modifier.padding(MaterialTheme.space.Normal),
                onClick = {
                   swipeState = SwipeState.NONE
                }
            ) {
                Text(text = "Reset Swipe")
            }
        }
}