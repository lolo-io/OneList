package com.lolo.io.onelist.feature.lists.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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
    drawItem: @Composable() (SwipeableRowScope.(T) -> Unit),
    state: SwipableListState<T>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier)  {
        items(items) { item ->
            val scope = swipeableRowScope(
                swipeState = state.itemsStates.get(item) ?: SwipeState.NONE,
                setSwipeState = {
                    state.setItemSwipe(item, it)
                }
            )

            drawItem(scope, item)
        }
    }
}
@Preview
@Composable
private fun Preview_SwipeableList() {

}
