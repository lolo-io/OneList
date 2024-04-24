package com.lolo.io.onelist.feature.lists.components.list_chips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.model.previewMany
import com.lolo.io.onelist.core.ui.composables.ComposePreview
import com.lolo.io.onelist.feature.lists.components.core.reorderable_flow_row.DraggableFlowRow
import com.lolo.io.onelist.feature.lists.components.core.reorderable_flow_row.ReorderableFlowRowItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ListsFlowRow(
    lists: List<ItemList>,
    selectedList: ItemList,
    modifier : Modifier = Modifier,
    onClick: (ItemList) -> Unit,
    onLongClick: (ItemList) -> Unit = {},
    onListReordered: (List<ItemList>) -> Unit = {},
) {
    val haptic = LocalHapticFeedback.current
    var isDragging by remember { mutableStateOf(false) }
    var debounceClickLongClick by remember { mutableStateOf<Job?>(null) }
    val bgScope = remember {
        CoroutineScope(Dispatchers.Default)
    }

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
            ListChip(label = list.title, state,
                onClick = {
                if(!isDragging && debounceClickLongClick?.isActive != true) {
                    onClick(list)
                } })
        },
        drawDragItem = {
            ListChip(label = it.title, ListChipState.DRAGGED)
        },
        onDragStart = {
            onLongClick(it)
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            isDragging = true
        },
        onDragEnd = {
            isDragging = false
            debounceClickLongClick = bgScope.launch { delay(300) }
        },
        onDragCancel = {
            isDragging = false
            debounceClickLongClick = bgScope.launch { delay(300) }
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