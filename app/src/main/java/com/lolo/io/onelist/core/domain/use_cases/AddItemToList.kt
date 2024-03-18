package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList

class AddItemToList(
    val saveListToDb: SaveListToDb
) {
    suspend operator fun invoke(list: ItemList, item: Item): ItemList {
        list.items = listOf(item) + list.items
        saveListToDb(list)
        return list
    }
}