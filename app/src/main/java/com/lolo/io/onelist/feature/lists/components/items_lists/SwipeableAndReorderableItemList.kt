package com.lolo.io.onelist.feature.lists.components.items_lists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.lolo.io.onelist.feature.lists.components.core.DraggableItem
import com.lolo.io.onelist.feature.lists.components.core.draggableItem
import com.lolo.io.onelist.feature.lists.components.core.rememberDraggableListState
import com.lolo.io.onelist.feature.lists.components.core.rememberSwipeableListState
import kotlinx.coroutines.delay


@Composable
fun SwipeableAndReorderableItemList(
    items: List<Item>,
    modifier: Modifier = Modifier,
    onItemSwipedToStart: (Item) -> Unit = {},
    onClickOnItem: (Item) -> Unit = {},
    state: SwipableListState<Item> = rememberSwipeableListState(),
    onListReordered: (List<Item>, draggedItem: DraggableItem<Item>) -> Unit = { _, _ -> },
    onShowOrHideComment: (Item) -> Unit = {},
    refreshing: Boolean = false,
    onRefresh: () -> Unit = {},
) {


    val draggableListState = rememberDraggableListState(
        items,
        onListReordered = { list, draggedItem ->
            onListReordered(list, draggedItem)
            state.scrollTo(draggedItem.bounds)
        }
    )


    var isSwiping by remember { mutableStateOf(false) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(items) {
        draggableListState.setItems(items)
    }


    DraggableAndSwipeableList(
        modifier = modifier,
        itemKeys = { it.id },
        isSwiping = isSwiping,
        draggableListState = draggableListState,
        drawItem = { draggableItem ->
            SwipeableItem(
                onIsSwiping = {
                    isSwiping = it
                },
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
                    if (!isDragging && !isSwiping) {
                        onClickOnItem(draggableItem.item)
                    }
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
        onIsDragging = {
            isDragging = it
        },
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
