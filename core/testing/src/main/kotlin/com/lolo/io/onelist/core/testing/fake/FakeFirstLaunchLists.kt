package com.lolo.io.onelist.core.testing.fake

import com.lolo.io.onelist.core.model.FirstLaunchLists
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.testing.data.testLists

class FakeFirstLaunchLists : FirstLaunchLists {
    override fun firstLaunchLists(): List<ItemList> {
        return testLists
    }
}