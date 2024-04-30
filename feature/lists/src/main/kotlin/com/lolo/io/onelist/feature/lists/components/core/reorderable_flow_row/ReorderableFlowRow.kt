package com.lolo.io.onelist.feature.lists.components.core.reorderable_flow_row

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.lolo.io.onelist.core.data.utils.TestTags

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
    onListReordered: (List<T>) -> Unit = { _ -> },
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
) {


    val draggableListState = rememberReorderableFlowRowState(
        items = items,
    )

    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        FlowRow(
            modifier = modifier
                .reorderableFlowRow(
                    reorderableFlowLayoutState = draggableListState,
                    onDragStart = onDragStart,
                    onDragEnd = {
                        if (draggableListState.items != items) {
                            onListReordered(draggableListState.items)
                        }
                        onDragEnd()
                    },
                    onDragCancel = {
                        /*
                        if(draggableListState.items != items) {
                            onListReordered(draggableListState.items)
                        }
                         */
                        onDragCancel()
                    }
                )
                .fillMaxWidth(),
            horizontalArrangement = horizontalArrangement,
            verticalArrangement = verticalArrangement,
            maxItemsInEachRow = maxItemsInEachRow
        ) {
            draggableListState.reorderableItems.map { draggableItem ->
                key(itemKeys(draggableItem.item)) {
                    Box(
                        modifier = Modifier.reorderableItemInFlowRow(draggableItem)
                            .testTag(TestTags.FlowRowItem)
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
                modifier = Modifier.draggedItemInFlowRow(draggableListState, draggedItem)
            ) {
                drawDragItem(draggedItem.item)
            }
        }
    }
}



