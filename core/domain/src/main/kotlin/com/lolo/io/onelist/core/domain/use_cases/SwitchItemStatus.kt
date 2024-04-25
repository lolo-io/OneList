package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList

class SwitchItemStatus(
    val saveListToDb: SaveListToDb
) {
    suspend operator fun invoke(list: ItemList, item: Item): ItemList {
        val doneStatus = !item.done
        list.items =
            when (doneStatus) {
                true -> list.items.filter { it != item } + item.copy(done = true)
                else -> listOf(item.copy(done = false)) + list.items.filter { it != item }
            }
        saveListToDb(list)
        return list
    }
}