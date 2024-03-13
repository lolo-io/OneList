package com.lolo.io.onelist.feature.lists.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.lolo.io.onelist.core.model.ItemList

@Composable
fun ListsFlowRow(
    lists: List<ItemList>,
    selectedList: ItemList,
    onClick: (ItemList) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    DraggableFlowRow(
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
            onClick(it)
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        horizontalArrangement = Arrangement.Center,
    )
}