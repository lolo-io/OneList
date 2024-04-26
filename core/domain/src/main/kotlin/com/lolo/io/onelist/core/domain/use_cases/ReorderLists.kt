package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.data.reporitory.OneListRepository

class ReorderLists(
    val repository: OneListRepository,
) {
    suspend operator fun invoke(lists: List<ItemList>, selectedList: ItemList): List<ItemList> {
        return lists.also {
            repository.saveAllLists(it.mapIndexed { index, itemList -> itemList.apply {
                itemList.position = index
            } })
            repository.selectList(selectedList)
        }
    }
}