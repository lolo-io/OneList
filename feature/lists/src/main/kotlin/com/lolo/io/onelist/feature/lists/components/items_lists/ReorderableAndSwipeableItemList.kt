package com.lolo.io.onelist.feature.lists.components.items_lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.preview
import com.lolo.io.onelist.core.designsystem.preview.ThemedPreview
import com.lolo.io.onelist.feature.lists.components.core.SwipeState
import com.lolo.io.onelist.feature.lists.components.core.reorderable_swipeable_list.ReorderableList
import com.lolo.io.onelist.feature.lists.components.core.reorderable_swipeable_list.hijacker.rememberLazyListStateHijacker
import com.lolo.io.onelist.feature.lists.components.core.reorderable_swipeable_list.swipeableRowScope
import com.lolo.io.onelist.feature.lists.components.dialogs.components.ScopedComposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SwipeableLazyListState(
    val listState: LazyListState
) {
    val swipeStates = mutableStateMapOf<Long, SwipeState>()

    fun setSwipeState(item: Item, state: SwipeState) {
        swipeStates[item.id] = state
    }

    fun resetSwipeState(item: Item) {
        swipeStates[item.id] = SwipeState.NONE
    }
}

@Composable
fun rememberSwipeableLazyListState(): SwipeableLazyListState {
    val listState = rememberLazyListState()

    return remember { // todo add Saved to hold swipe after configuration change
        SwipeableLazyListState(listState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReorderableAndSwipeableItemList(
    items: List<Item>,
    modifier: Modifier = Modifier,
    onItemSwipedToStart: (Item) -> Unit = {},
    onItemSwipedToEnd: (Item) -> Unit = {},
    onItemSwipedBackToCenter: (Item) -> Unit = {},
    onClickOnItem: (Item) -> Unit = {},
    onListReordered: (List<Item>) -> Unit = { },
    onShowOrHideComment: (Item) -> Unit = {},
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    swipeableListState: SwipeableLazyListState = rememberSwipeableLazyListState(),
) {

    val pullRefreshState = rememberPullToRefreshState()

    val coroutineScope = rememberCoroutineScope()

    var isReordering by remember { mutableStateOf(false) }

    var enableItemClick by remember { mutableStateOf(true) }

    LaunchedEffect(isReordering) {
        enableItemClick = if (isReordering) false else {
            delay(200)
            true
        }
    }

    Box(
        modifier
            .fillMaxSize()
            .nestedScroll(pullRefreshState.nestedScrollConnection)
    ) {
        rememberLazyListStateHijacker(
            listState = swipeableListState.listState,
            enabled = !isReordering
        )
        ReorderableList(
            modifier = modifier,
            listState = swipeableListState.listState,
            items = items,
            itemKey = { it.id },
            canReorder = { item1, item2 -> item1.done == item2.done },
            onListReordered = onListReordered,
            onReordering = {
                isReordering = it
            },
            drawRow = { item ->
                ScopedComposable(scope = swipeableRowScope(
                    swipeState = swipeableListState.swipeStates[item.id] ?: SwipeState.NONE,
                    setSwipeState = {
                        swipeableListState.setSwipeState(item, it)
                    }
                )) {
                    SwipeableItem(
                        item = item,
                        onSwipedToStart = {
                            onItemSwipedToStart(item)
                        },
                        onSwipedToEnd = {
                            onItemSwipedToEnd(item)
                        },
                        onSwipedBackToCenter = {
                            onItemSwipedBackToCenter(item)
                        }
                    ) {
                        ItemUI(
                            item,
                            onClick = {
                                coroutineScope.launch {
                                    if (enableItemClick) {
                                        onClickOnItem(item)
                                    }
                                }
                            },
                            onClickDisplayComment = {
                                onShowOrHideComment(item)
                            },
                        )
                    }
                }
            },
        )

        if (pullRefreshState.isRefreshing) {
            if (pullRefreshState.isRefreshing) {
                LaunchedEffect(true) {
                    onRefresh()
                }
            }
        }

        LaunchedEffect(isRefreshing) {
            if (isRefreshing)
                pullRefreshState.startRefresh()
            else
                pullRefreshState.endRefresh()

        }

        if (pullRefreshState.verticalOffset > 0) {
            PullToRefreshContainer(
                pullRefreshState,
                Modifier
                    .align(Alignment.TopCenter),
                indicator = {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(8.dp),
                    )
                }
            )
        }
    }
}


@Preview
@Composable
private fun Preview_SwipeableItemList() = ThemedPreview {
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

    Column {
        ReorderableAndSwipeableItemList(
            items = items
        )
    }
}
