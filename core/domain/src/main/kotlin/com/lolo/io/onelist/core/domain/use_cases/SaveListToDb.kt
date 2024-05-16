package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.data.repository.OneListRepository

interface SaveListToDb {
    suspend operator fun invoke(itemList: ItemList)
}


class SaveListToDbImpl(private val repository: OneListRepository): SaveListToDb {
    override suspend operator fun invoke(itemList: ItemList) {
        return repository.saveList(itemList)
    }
}