package com.lolo.io.onelist.core.domain.use_cases

import android.util.Log
import com.google.firebase.annotations.concurrent.Background
import com.lolo.io.onelist.core.data.persistence.PersistenceHelper
import com.lolo.io.onelist.core.model.ItemList
import kotlinx.coroutines.delay

class HandleFirstLaunch(private val persistenceHelper: PersistenceHelper) {
    suspend operator fun invoke(lists: List<ItemList>) {
        delay(200)
        if (persistenceHelper.firstLaunch) {
            lists.forEach {
                persistenceHelper.upsertList(it)
            }
            persistenceHelper.firstLaunch = false
        }
    }
}