package com.lolo.io.onelist.feature.lists.components.items_lists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lolo.io.onelist.core.design.space
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.preview
import com.lolo.io.onelist.core.ui.composables.ComposePreview
import com.lolo.io.onelist.feature.lists.components.core.SwipableListState
import com.lolo.io.onelist.feature.lists.components.core.DraggableAndSwipeableList
import com.lolo.io.onelist.feature.lists.components.core.draggableItem
import com.lolo.io.onelist.feature.lists.components.core.rememberDraggableListState
import com.lolo.io.onelist.feature.lists.components.core.rememberSwipeableListState
import kotlinx.coroutines.delay


@Composable
fun SwipeableAndReorderableItemList(
    items: List<Item>,
    onItemSwipedToStart: (Item) -> Unit = {},
    onClickOnItem: (Item) -> Unit = {},
    state: SwipableListState<Item> = rememberSwipeableListState<Item>(),
    modifier: Modifier = Modifier,
    onListReordered: (List<Item>) -> Unit = {},
    onShowOrHideComment: (Item) -> Unit = {},
    refreshing: Boolean = false,
    onRefresh: () -> Unit = {},
) {

    val draggableListState = rememberDraggableListState(
        items,
        onListReordered = onListReordered
    )

    LaunchedEffect(items) {
        draggableListState.setItems(items)
    }

    DraggableAndSwipeableList(
        modifier = modifier,
        itemKeys = { it.id },
        draggableListState = draggableListState,
        drawItem = { draggableItem ->
            SwipeableItem(
                modifier = Modifier
                    .draggableItem(draggableListState, draggableItem)
                    .then(
                        if (draggableListState.draggedItem != null &&
                            draggableListState.draggedItem == draggableItem
                        ) Modifier.alpha(0.2f) else Modifier
                    ),
                item = draggableItem.item,
                onSwipedToStart = {
                    onItemSwipedToStart(draggableItem.item)
                },
                onClick = {
                    onClickOnItem(draggableItem.item)
                },
                onShowOrHideComment = {
                    onShowOrHideComment(draggableItem.item)
                })

        },
        drawDraggedItem = { draggedItem ->
            Surface(shadowElevation = 24.dp) {
                ItemRow(draggedItem.item)
            }
        },
        refreshing = refreshing,
        onRefresh = onRefresh,

        state = state,
    )
}


@Preview
@Composable
private fun Preview_SwipeableItemList() = ComposePreview {
    val itemListSwipeState = rememberSwipeableListState<Item>()

    val items = listOf(
        Item.preview.copy(id = 1),
        Item.preview.copy(
            id = 2,
            title = "Long Long Long Long Long Long Long Long Long Long Long Long Long "
        ),
        Item.preview.copy(
            id = 3,
            title = "Long Long Long Long Long Long Long Long Long Long Long Long Long "
        ),
        Item.preview.copy(id = 4),
        Item.preview.copy(id = 5)
    )

    LaunchedEffect(true) {
        delay(5000)
        itemListSwipeState.resetItemSwipe(items.get(0))
    }

    Column {
        SwipeableAndReorderableItemList(
            items = items,
            state = itemListSwipeState,
        )

        Button(
            modifier = Modifier.padding(MaterialTheme.space.Normal),
            onClick = {
                items.forEach {
                    itemListSwipeState.resetItemSwipe(it)
                }
            }

        ) {
            Text(text = "Reset Swipes")
        }
    }
}
