package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList

class RemoveItemFromList (
    val saveListToDb: SaveListToDb
) {
    suspend operator fun invoke(list: ItemList, item: Item): ItemList {
        list.items -= item
        saveListToDb(list)
        return list
    }
}