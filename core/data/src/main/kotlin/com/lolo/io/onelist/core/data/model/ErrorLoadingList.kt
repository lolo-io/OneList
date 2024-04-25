package com.lolo.io.onelist.core.data.model

sealed  class ErrorLoadingList {
    data object FileMissingError: ErrorLoadingList()
    data object FileCorruptedError: ErrorLoadingList()
    data object PermissionDeniedError: ErrorLoadingList()
    data object UnknownError: ErrorLoadingList()
}