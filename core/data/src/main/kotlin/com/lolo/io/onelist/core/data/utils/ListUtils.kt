package com.lolo.io.onelist.core.data.utils

fun <T> List<T>.updateOneIf(newItem: T, condition: (T) -> Boolean): List<T> {
    return this.map {
        if (condition(it)) {
            newItem
        } else it
    }
}