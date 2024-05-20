package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.data.repository.OneListRepository
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import com.lolo.io.onelist.core.model.ItemList
import kotlinx.coroutines.delay

class HandleFirstLaunch(private val repository: OneListRepository,
                        private val preferencesHelper: SharedPreferencesHelper
) {
    suspend operator fun invoke(lists: List<ItemList>): Boolean {
        val firstLaunch = preferencesHelper.firstLaunch
        if (firstLaunch) {
            lists.forEach {
                repository.createList(it)
            }
            preferencesHelper.firstLaunch = false
        }
        return preferencesHelper.firstLaunch
    }
}