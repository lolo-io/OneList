package com.lolo.io.onelist.feature.lists.components.core

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> DraggableFlowRow(
    items: List<T>,
    itemKeys: (item: T) -> Any,
    modifier: Modifier = Modifier,
    drawItem: @Composable (item: T, isDragged: Boolean) -> Unit = { _, _ -> },
    drawDragItem: @Composable (item: T) -> Unit = {},
    onDragStart: (item: T) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDragCancel: () -> Unit = {},
    onListReordered: (List<T>, item: DraggableItem<T>) -> Unit = {_,_, -> },
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
) {

    val draggableListState = rememberDraggableListState(
        items = items,
        orientation = DraggableListState.Orientation.VERTICAL,
        onListReordered = onListReordered

    )

    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        FlowRow(
            modifier = modifier.draggableItemList(
                draggableListState = draggableListState,
                onDragStart = onDragStart,
                onDragEnd = onDragEnd,
                onDragCancel = onDragCancel
            ).fillMaxWidth(),
            horizontalArrangement = horizontalArrangement,
            verticalArrangement = verticalArrangement,
            maxItemsInEachRow = maxItemsInEachRow
        ) {
            draggableListState.draggableItems.map { draggableItem ->
                key(itemKeys(draggableItem.item)) {
                    Box(
                        modifier = Modifier.draggableItem(draggableListState, draggableItem)
                    ) {
                        drawItem(
                            draggableItem.item,
                            draggableItem == draggableListState.draggedItem
                        )
                    }
                }
            }
        }

        draggableListState.draggedItem?.let { draggedItem ->
            Box(
                modifier = Modifier.draggedItem(draggableListState, draggedItem)
            ) {
                drawDragItem(draggedItem.item)
            }
        }
    }
}



