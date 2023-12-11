package com.lolo.io.onelist.feature.lists

data class UIState (
    val showAddCommentArrow: Boolean = false,
    val showValidate: Boolean = false,
    val isRefreshing: Boolean = false,
    val addCommentText: String = "",
    val showButtonClearComment: Boolean = false,
)