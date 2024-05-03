package com.lolo.io.onelist.feature.lists.components.items_lists

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.lolo.io.onelist.core.data.utils.TestTags
import com.lolo.io.onelist.core.designsystem.Palette
import com.lolo.io.onelist.core.designsystem.colors.appColors
import com.lolo.io.onelist.core.designsystem.space
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.designsystem.preview.ThemedPreview
import com.lolo.io.onelist.feature.lists.components.core.SwipeState
import com.lolo.io.onelist.feature.lists.components.core.reorderable_swipeable_list.SwipeableRow
import com.lolo.io.onelist.feature.lists.components.core.reorderable_swipeable_list.SwipeableRowScope

@Composable
internal fun SwipeableRowScope.SwipeableItem(
    item: Item,
    modifier: Modifier = Modifier,
    onSwipedToStart: () -> Unit = {},
    onSwipedToEnd: () -> Unit = {},
    onSwipedBackToCenter: () -> Unit = {},
    drawItem: @Composable (Item) -> Unit = {}
) {

    SwipeableRow(
        modifier = modifier.testTag(TestTags.SwipeableItem),
        backgroundStartToEnd = {
            Box(
                Modifier
                    .background(MaterialTheme.appColors.swipeEditBackground)
                    .fillMaxSize()
                    .padding(start = MaterialTheme.space.Normal)
                    .testTag(TestTags.SwipeableItemEditBackground),
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
                    .fillMaxSize()
                    .testTag(TestTags.SwipeableItemDeleteBackground),
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
        },
        onSwipedBackToCenter = {
            setSwipeState(SwipeState.NONE)
            onSwipedBackToCenter()
        }
    ) {
        drawItem(item)
    }
}

@Preview
@Composable
private fun Preview_SwipeableItem() = ThemedPreview {
/*
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

 */
}