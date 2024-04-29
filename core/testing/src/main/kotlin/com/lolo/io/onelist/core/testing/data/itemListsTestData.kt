package com.lolo.io.onelist.core.testing.data

import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.model.preview

val testLists = (0..<5).map { listIndex ->
    createTestList(listIndex + 1)
}

fun createTestList(
    position: Int = 0,
    id: Long = position.toLong(),
    hasComments: Boolean = false
): ItemList = ItemList(
    title = "Item List $position",
    position = position,
    items = (0..5).map { itemIndex ->
        Item.preview.copy(
            title = "Test Item $position - $itemIndex",
            id = position * 10L + itemIndex,
            comment = if (hasComments) "Test Item $position - $itemIndex Comment" else ""
        )
    },
    uri = null,
    id = id
)

fun createFakeListWhereAllItemsHaveComment() =
    listOf(
        createTestList(
            hasComments = true
        )
    )