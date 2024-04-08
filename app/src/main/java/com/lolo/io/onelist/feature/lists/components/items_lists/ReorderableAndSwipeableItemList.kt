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
import com.lolo.io.onelist.core.ui.composables.ComposePreview
import com.lolo.io.onelist.feature.lists.components.core.SwipeState
import com.lolo.io.onelist.feature.lists.components.core.reorderable_swipeable_list.ReorderableList
import com.lolo.io.onelist.feature.lists.components.core.reorderable_swipeable_list.hijacker.rememberLazyListStateHijacker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SwipeableLazyListState(
    val listState: LazyListState
) {
    private val swipeStates: HashMap<Long, SwipeState> = hashMapOf()
    fun setSwipeState(item: Item, state: SwipeState) {
        swipeStates[item.id] = state
    }
    fun resetSwipeState(item: Item) {
        swipeStates[item.id] = SwipeState.NONE
    }
    fun getSwipeState(item: Item) : SwipeState {
        return swipeStates[item.id] ?: SwipeState.NONE
    }
}

@Composable
fun rememberSwipeableLazyListState(): SwipeableLazyListState  {
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
    onClickOnItem: (Item) -> Unit = {},
    onListReordered: (List<Item>) -> Unit = { },
    onShowOrHideComment: (Item) -> Unit = {},
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    swipeableListState: SwipeableLazyListState = rememberSwipeableLazyListState(),
) {

    val pullRefreshState = rememberPullToRefreshState()

    val coroutineScope = rememberCoroutineScope()

    var isSwiping by remember { mutableStateOf(false) }
    var isReordering by remember { mutableStateOf(false) }

    var enableItemClick by remember { mutableStateOf(true) }

    LaunchedEffect(isSwiping, isReordering) {
        enableItemClick = if (isSwiping || isReordering) false else {
            delay(200)
            true
        }
    }

    Box(
        modifier
            .fillMaxSize()
            .nestedScroll(pullRefreshState.nestedScrollConnection)
    ) {
        rememberLazyListStateHijacker(listState = swipeableListState.listState, enabled = !isReordering)
        ReorderableList(
            modifier = modifier,
            listState = swipeableListState.listState,
            items = items,
            itemKey = { it.id },
            canReorder = { item1, item2 -> item1.done == item2.done },
            enableReorder = !isSwiping,
            userScrollEnabled = !isSwiping,
            onListReordered = onListReordered,
            onReordering = {
                isReordering = it
            },
            drawRow = { item ->

                SwipeableItem(
                    item = item,
                    swipeState = swipeableListState.getSwipeState(item),
                    setSwipeState = {
                        swipeableListState.setSwipeState(item, it)
                    },
                    onIsSwiping = {
                        isSwiping = it
                    },
                    onSwipedToStart = {
                        onItemSwipedToStart(item)
                    },
                    onSwipedToEnd = {
                        onItemSwipedToEnd(item)
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
private fun Preview_SwipeableItemList() = ComposePreview {
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
