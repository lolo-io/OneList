package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList

class EditItemOfList(
    val saveListToDb: SaveListToDb
) {
    suspend operator fun invoke(list:ItemList, item:Item):ItemList {
        val itemIndex =
            list.items.indexOfFirst { it.id == item.id } // find by id because equals isn't true here

        if (itemIndex > -1) {
            list.items =
                list.items.subList(0, itemIndex) + item + list.items.subList(
                    itemIndex + 1,
                    list.items.size
                )
            saveListToDb(list)
        }
        return list
    }
}