package com.lolo.io.onelist.feature.lists.components.core

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SwipableListState<T>(
    val itemsStates: SnapshotStateMap<T, SwipeState> = SnapshotStateMap(),
    val scrollState: ScrollState,
    private val coroutineScope: CoroutineScope
) {
    fun resetItemSwipe(item: T) {
        itemsStates += Pair(item, SwipeState.NONE)
    }

    fun setItemSwipe(item: T, swipeState: SwipeState) {
        itemsStates += Pair(item, swipeState)
    }

    fun scrollToBottom() {
        coroutineScope.launch {
            delay(200)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    fun scrollToTop() {
        coroutineScope.launch {
            delay(200)
            scrollState.animateScrollTo(0)
        }
    }

    fun scrollTo(itemBounds: androidx.compose.ui.geometry.Rect) {
        coroutineScope.launch {
            while (itemBounds.bottom > scrollState.viewportSize / 1.5f + scrollState.value) {
                scrollState.animateScrollBy(itemBounds.height)
            }

            while (itemBounds.top < scrollState.viewportSize / 3f) {
                scrollState.animateScrollBy(-itemBounds.height)
            }
        }
    }
}

@Composable
fun <T> rememberSwipeableListState(): SwipableListState<T> {

    val itemsStates = remember {
        mutableStateMapOf<T, SwipeState>()
    }

    return SwipableListState(
        itemsStates,
        rememberScrollState(),
        rememberCoroutineScope()
    )
}


@OptIn(ExperimentalMaterial3Api::class)
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
    onRefresh: () -> Unit = {},
    onIsDragging: (Boolean) -> Unit = {}
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


    LaunchedEffect(draggableListState.draggedItem) {
        val isDragging = if (draggableListState.draggedItem != null) true
        else {
            delay(2000)
            false
        }

        onIsDragging(isDragging)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(pullRefreshState.nestedScrollConnection)
    ) {
        val haptic = LocalHapticFeedback.current

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(state.scrollState)
                .draggableItemList(
                    enableDrag = !isSwiping,
                    draggableListState = draggableListState,
                    onDragStart = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
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
                modifier = Modifier.draggedItemVertical(
                    draggableListState, draggedItem,
                    scrollOffset = state.scrollState.value.toFloat()
                )
            ) {

                drawDraggedItem(draggedItem)
            }
        }


        if (pullRefreshState.verticalOffset > 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(pullRefreshState.progress - 0.5f)
            ) {
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
