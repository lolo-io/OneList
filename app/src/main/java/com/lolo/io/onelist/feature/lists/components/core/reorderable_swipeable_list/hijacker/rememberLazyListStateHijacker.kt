package com.lolo.io.onelist.feature.lists.components.core.reorderable_swipeable_list.hijacker

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import me.gingerninja.lazylist.hijacker.LazyListStateHijacker

@Composable
fun rememberLazyListStateHijacker(
    listState: LazyListState,
    enabled: Boolean = true
): LazyListStateHijacker {
    return remember(listState, enabled) {
        LazyListStateHijacker(listState, enabled)
    }.apply {
        this.enabled = enabled
    }
}