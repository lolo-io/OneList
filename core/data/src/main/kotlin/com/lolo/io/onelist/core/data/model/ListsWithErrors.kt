package com.lolo.io.onelist.core.data.model

import com.lolo.io.onelist.core.model.ItemList

data class ListsWithErrors(
    val lists: List<ItemList> = listOf(),
    val errors: List<ErrorLoadingList> = listOf(),
)