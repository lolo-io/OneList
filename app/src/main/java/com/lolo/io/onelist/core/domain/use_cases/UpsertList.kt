package com.lolo.io.onelist.core.domain.use_cases

import android.net.Uri
import com.lolo.io.onelist.core.data.persistence.PersistenceHelper
import com.lolo.io.onelist.core.model.ItemList

class UpsertList(private val persistenceHelper: PersistenceHelper) {

    // todo add a repository

    suspend operator fun invoke(itemList: ItemList): ItemList {
        return persistenceHelper.upsertList(itemList)
    }

    suspend operator fun invoke(uri: Uri): ItemList {
        return persistenceHelper.createListFromUri(uri)
    }
}