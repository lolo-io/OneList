package com.lolo.io.onelist.feature.lists.components.core.reorderable_swipeable_list

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lolo.io.onelist.core.designsystem.space
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.ui.composables.ComposePreview
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyColumnState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> ReorderableList(
    items: List<T>,
    itemKey: (T) -> Any,
    modifier: Modifier = Modifier,
    canReorder: (T, T) -> Boolean = { _, _ -> true },
    interactionSources: List<MutableInteractionSource>? = null,
    drawRow: @Composable (T) -> Unit = {},
    enableReorder: Boolean = true,
    userScrollEnabled: Boolean = true,
    onListReordered: (List<T>) -> Unit = {},
    onReordering: (Boolean) -> Unit = {},
    listState: LazyListState = rememberLazyListState()
) {
    val view = LocalView.current

    val list =
        remember(items) { mutableStateOf(items) }
    val reorderableLazyColumnState = rememberReorderableLazyColumnState(listState) { from, to ->
        if (canReorder(list.value[from.index], list.value[to.index])) {
            list.value = list.value.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                view.performHapticFeedback(HapticFeedbackConstants.SEGMENT_FREQUENT_TICK)
            } else {
                view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            }
            onListReordered(list.value)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        userScrollEnabled = userScrollEnabled,
        state = listState,
        contentPadding = PaddingValues(top = 1.dp),
    ) {
        items(list.value, key = { itemKey(it) }) { item ->

            ReorderableItem(reorderableLazyColumnState, key = itemKey(item)) { isDragging ->
                val elevation by animateDpAsState(
                    if (isDragging) 4.dp else 0.dp,
                    label = "elevation"
                )

                Surface(shadowElevation = elevation) {
                    Row(
                        modifier = Modifier
                            .longPressDraggableHandle(
                                enabled = enableReorder,
                                interactionSource = interactionSources?.getOrNull(items.indexOf(item)),
                                onDragStarted = {
                                    onReordering(true)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                        view.performHapticFeedback(HapticFeedbackConstants.DRAG_START)
                                    } else {
                                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    }
                                },
                                onDragStopped = {
                                    onReordering(false)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                        view.performHapticFeedback(HapticFeedbackConstants.GESTURE_END)
                                    } else {
                                        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                                    }
                                },
                            ),
                    ) {
                        drawRow(item)
                    }
                }
            }
        }
        item {
            Spacer(
                Modifier.windowInsetsBottomHeight(
                    WindowInsets.systemBars
                )
            )
        }

    }
}



@Preview
@Composable
private fun Preview_ReorderableList() = ComposePreview {
    val items =
        remember { mutableStateOf(List(100) {
            com.lolo.io.onelist.core.model.Item(
                title = "\"Item $it\"",
                id = it.toLong()
            )
        }) }
    ReorderableList(
        items = items.value,
        itemKey = { it.id },
        drawRow = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = MaterialTheme.space.Normal)
            ) {
                Text(text = it.title)
            }
        },
    )
}
