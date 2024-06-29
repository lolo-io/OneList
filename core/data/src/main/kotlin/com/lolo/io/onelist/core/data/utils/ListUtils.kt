package com.lolo.io.onelist.core.data.utils

import com.lolo.io.onelist.core.model.ItemList

fun <T> List<T>.updateOneIf(newItem: T, condition: (T) -> Boolean): List<T> {
    return this.map {
        if (condition(it)) {
            newItem
        } else it
    }
}

inline fun <T, R> Iterable<T>.allUniqueBy(transform: (T) -> R): Boolean {
    val hashset = hashSetOf<R>()
    return this.all { hashset.add(transform(it)) }
}

fun ensureAllItemsIdsAreUnique(itemList: ItemList): ItemList {
    return if(!itemList.items.allUniqueBy { it.id }) {
        itemList.copy(items = itemList.items.distinctBy { it.id })
    } else itemList
}