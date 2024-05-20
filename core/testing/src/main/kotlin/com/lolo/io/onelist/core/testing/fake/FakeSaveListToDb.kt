package com.lolo.io.onelist.core.testing.fake

import com.lolo.io.onelist.core.domain.use_cases.SaveListToDb
import com.lolo.io.onelist.core.model.ItemList

class FakeSaveListToDb : SaveListToDb {

    var hasBeenCalled: Boolean = false
        private set

    override suspend fun invoke(itemList: ItemList) {
        hasBeenCalled = true
    }
}