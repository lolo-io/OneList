package com.lolo.io.onelist.core.testing.data

import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.model.preview

val testLists = (0..<5).map { listIndex ->
    createTestList(listIndex + 1)
}

fun createTestList(
    position: Int,
    id: Long = position.toLong()
): ItemList = ItemList(
    title = "Item List $position",
    position = position,
    items = (0..5).map { itemIndex ->
        Item.preview.copy(
            title = "Test Item $position - $itemIndex",
            id = position * 10L + itemIndex,
        )
    },
    uri = null,
    id = id
)