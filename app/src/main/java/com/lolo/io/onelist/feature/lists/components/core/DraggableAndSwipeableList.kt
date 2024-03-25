package com.lolo.io.onelist.feature.lists.components.core

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

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


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun <T> DraggableAndSwipeableList(
    itemKeys: (item: T) -> Any,
    draggableListState: DraggableListState<T>,
    drawItem: @Composable() (SwipeableRowScope.(DraggableItem<T>) -> Unit),
    drawDraggedItem: @Composable() (DraggableItem<T>) -> Unit,
    state: SwipableListState<T>,
    modifier: Modifier = Modifier,
    refreshing: Boolean = false,
    isSwiping: Boolean = false,
    onRefresh: () -> Unit = {}
) {

    val pullRefreshState = rememberPullToRefreshState()

    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            onRefresh()
        }
    }

    LaunchedEffect(refreshing) {
        if (refreshing)
            pullRefreshState.startRefresh()
        else
            pullRefreshState.endRefresh()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(pullRefreshState.nestedScrollConnection)
    ) {
        val haptic = LocalHapticFeedback.current
        val scrollState = rememberScrollState()

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .draggableItemList(
                    enableDrag = !isSwiping,
                    draggableListState = draggableListState,
                    onDragStart = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                ),

            ) {

            draggableListState.draggableItems.map { draggableItem ->
                key(itemKeys(draggableItem.item)) {
                    val scope = swipeableRowScope(
                        swipeState = state.itemsStates.get(draggableItem.item) ?: SwipeState.NONE,
                        setSwipeState = {
                            state.setItemSwipe(draggableItem.item, it)
                        }
                    )
                    drawItem(scope, draggableItem)
                }
            }
        }

        draggableListState.draggedItem?.let { draggedItem ->
            Box(
                modifier = Modifier.draggedItemVertical(draggableListState, draggedItem)
            ) {

                drawDraggedItem(draggedItem)
            }
        }


        if (pullRefreshState.verticalOffset > 0) {
            Box(modifier = Modifier
                .fillMaxSize()
                .alpha(pullRefreshState.progress - 0.5f)) {
                PullToRefreshContainer(
                    pullRefreshState,
                    modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-30).dp)
                        .zIndex(999f),

                    indicator = {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(8.dp),
                        )
                    })

            }
        }
    }


}

@Preview
@Composable
private fun Preview_SwipeableList() {

}
