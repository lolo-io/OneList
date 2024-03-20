package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.data.reporitory.OneListRepository
import com.lolo.io.onelist.core.model.ItemList

class SelectList(
    private val repository: OneListRepository
) {

    operator fun invoke(itemList: ItemList) {
        repository.selectList(itemList)
    }
}