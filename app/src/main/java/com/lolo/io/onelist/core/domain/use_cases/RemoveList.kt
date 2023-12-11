package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.data.persistence.PersistenceHelper
import com.lolo.io.onelist.core.model.ItemList

class RemoveList(private val persistenceHelper: PersistenceHelper) {

    // todo add a repository

    suspend operator fun invoke(itemList: ItemList, deleteBackupFile: Boolean) {
        persistenceHelper.deleteList(itemList, deleteBackupFile)
    }
}