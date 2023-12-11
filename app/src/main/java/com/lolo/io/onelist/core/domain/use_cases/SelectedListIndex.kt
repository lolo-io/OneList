package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.data.persistence.PersistenceHelper

class SelectedListIndex(private val persistenceHelper: PersistenceHelper) {

    // todo add a repository

    operator fun invoke(): Int {
        return persistenceHelper.selectedListIndex
    }

    operator fun invoke(index: Int) {
        persistenceHelper.selectedListIndex = index
    }
}