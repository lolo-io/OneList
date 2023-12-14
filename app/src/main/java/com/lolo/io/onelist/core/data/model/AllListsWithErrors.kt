package com.lolo.io.onelist.core.data.model

import com.lolo.io.onelist.core.model.ItemList

data class AllListsWithErrors(
    val lists: List<ItemList> = listOf(),
    val errors: List<ErrorLoadingList> = listOf(),
)