package com.lolo.io.onelist.feature.lists.components.list_chips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.model.previewMany
import com.lolo.io.onelist.core.ui.composables.ComposePreview
import com.lolo.io.onelist.feature.lists.components.core.DraggableFlowRow

@Composable
fun ListsFlowRow(
    lists: List<ItemList>,
    selectedList: ItemList,
    onClick: (ItemList) -> Unit,
    onLongClick: (ItemList) -> Unit = {},
    onListReordered: (List<ItemList>) -> Unit = {},
    modifier : Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current

    DraggableFlowRow(
        modifier = modifier,
        items = lists,
        itemKeys = { it.id },
        drawItem = { list, isDragged ->
            val state = when {
                isDragged -> ListChipState.SHADOW
                list == selectedList -> ListChipState.SELECTED
                else -> ListChipState.DEFAULT
            }
            ListChip(label = list.title, state, onClick = { onClick(list) })
        },
        drawDragItem = {
            ListChip(label = it.title, ListChipState.DRAGGED)
        },
        onDragStart = {
            onLongClick(it)
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        onListReordered = onListReordered,
        horizontalArrangement = Arrangement.Center,
    )
}

@Preview
@Composable
private fun Preview_ListsFlowRow() = ComposePreview {
    val lists = ItemList.previewMany(5)
    val selectedList = lists.get(0)
    ListsFlowRow(lists = lists, selectedList = selectedList,
        onClick = { showPreviewDialog(it.title) })
}