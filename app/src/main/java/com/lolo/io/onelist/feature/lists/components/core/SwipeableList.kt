package com.lolo.io.onelist.feature.lists.components.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.lolo.io.onelist.core.model.Item

class SwipableListState<T>(
    val itemsStates: SnapshotStateMap<T, SwipeState> = SnapshotStateMap()
) {
    fun resetItemSwipe(item: T) {
        itemsStates += Pair(item, SwipeState.NONE)
    }

    fun setItemSwipe(item: T, swipeState: SwipeState) {
        itemsStates += Pair(item, swipeState)
    }
}

@Composable
fun <T> rememberSwipeableListState(): SwipableListState<T> {
    val itemsStates = remember {
        mutableStateMapOf<T, SwipeState>()
    }

    return SwipableListState(itemsStates)
}


@Composable
fun <T> SwipeableList(
    items: List<T>,
    itemKeys: (item: T) -> Any,
    draggableListState: DraggableListState<T>,
    drawItem: @Composable() (SwipeableRowScope.(DraggableItem<T>) -> Unit),
    state: SwipableListState<T>,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier.draggableItemList(
            draggableListState = draggableListState
        ),
    ) {
        items(draggableListState.draggableItems) { draggableItem ->
            val scope = swipeableRowScope(
                swipeState = state.itemsStates.get(draggableItem.item) ?: SwipeState.NONE,
                setSwipeState = {
                    state.setItemSwipe(draggableItem.item, it)
                }
            )
            key(itemKeys(draggableItem.item)) {
                drawItem(scope, draggableItem)
            }
        }
    }

    draggableListState.draggedItem?.let { draggedItem ->
        Box(
            modifier = Modifier.draggedItem(draggableListState, draggedItem)
        ) {
            Text("DRAGGED ITEM")
        }
    }
}

@Preview
@Composable
private fun Preview_SwipeableList() {

}
