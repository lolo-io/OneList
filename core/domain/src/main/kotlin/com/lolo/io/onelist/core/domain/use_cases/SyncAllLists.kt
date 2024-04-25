package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.data.reporitory.OneListRepository

class SyncAllLists(private val repository: OneListRepository) {

    suspend operator fun invoke() {
        repository.syncAllLists()
    }
}