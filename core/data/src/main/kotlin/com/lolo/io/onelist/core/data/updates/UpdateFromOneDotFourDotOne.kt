package com.lolo.io.onelist.core.data.updates

import com.lolo.io.onelist.core.data.repository.OneListRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.security.SecureRandom

class UpdateFromOneDotFourDotOne(
    private val repository: OneListRepository
) {

    fun update(then: () -> Unit) {
        fixItemsWithSameIds(then)
    }

    private fun fixItemsWithSameIds(then: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val allLists = repository.getAllLists().first().lists
            val secureRandom = SecureRandom()
            val ids = allLists.flatMap { it.items }.map { it.id }
            val distinctIds = ids.distinct()
            if (ids.size > distinctIds.size) {
                allLists.forEach {
                    it.items.forEach {
                        it.id = secureRandom.nextLong()
                    }
                }

                repository.backupLists(allLists)

                then()
            }
        }
    }

}