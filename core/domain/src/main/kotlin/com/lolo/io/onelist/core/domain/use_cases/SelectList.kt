package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.data.repository.OneListRepository

class SelectList(
    private val repository: OneListRepository
) {

    operator fun invoke(itemList: ItemList) {
        repository.selectList(itemList)
    }
}