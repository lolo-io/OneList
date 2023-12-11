package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.data.persistence.PersistenceHelper
import com.lolo.io.onelist.core.model.ItemList

class GetAllLists(private val persistenceHelper: PersistenceHelper) {

    suspend operator fun invoke(): List<ItemList> {
        return persistenceHelper.getAllLists()
    }
}