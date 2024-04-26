package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.model.ItemList
import java.util.Collections
import com.lolo.io.onelist.core.data.reporitory.OneListRepository

class MoveList(private val repository: OneListRepository) {
    suspend operator fun invoke(fromPosition: Int, toPosition: Int, lists: List<ItemList>) {

        val tempAllList = ArrayList(lists)
        if (fromPosition in 0..<toPosition && toPosition < tempAllList.size) {
            for (i in fromPosition until toPosition) {
                Collections.swap(tempAllList, i, i + 1)
            }
        } else if (toPosition < tempAllList.size) {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(tempAllList, i, i - 1)
            }
        }
        tempAllList.forEachIndexed { i, list -> list.position = i + 1 }

     //   repository.saveAllLists(tempAllList.sortedBy { it.position })
     //   repository.selectList(toPosition)
    }
}