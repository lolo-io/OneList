package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList

class ClearList (
    val saveListToDb: SaveListToDb
) {
    suspend operator fun invoke(list: ItemList): ItemList {
        list.items = listOf()
        saveListToDb(list)
        return list
    }
}