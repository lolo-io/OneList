package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.data.repository.OneListRepository

class SaveListToDb(private val repository: OneListRepository) {

    suspend operator fun invoke(itemList: ItemList) {
        return repository.saveList(itemList)
    }
}