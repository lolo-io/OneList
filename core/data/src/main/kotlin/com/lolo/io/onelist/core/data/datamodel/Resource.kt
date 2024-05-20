package com.lolo.io.onelist.core.data.datamodel

sealed class Resource<out T> {
    data class Success<out T>(val value: T): Resource<T>()
    data object Loading: Resource<Nothing>()
    data class Error<out T>(val code: Int): Resource<T>()
}