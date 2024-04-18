package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList

class SetItemOfList(
    val saveListToDb: SaveListToDb
) {
    suspend operator fun invoke(list: ItemList, items: List<Item>): ItemList {
        list.items = items
        saveListToDb(list)
        return list
    }
}