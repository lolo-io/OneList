package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList

class SwitchItemCommentShown(
    val saveListToDb: SaveListToDb
) {
    suspend operator fun invoke(list: ItemList, item: Item): ItemList {
        list.items = list.items.map {
            if(item == it) {
                item.copy(commentDisplayed = !item.commentDisplayed)
            } else it
        }
        saveListToDb(list)
        return list
    }
}