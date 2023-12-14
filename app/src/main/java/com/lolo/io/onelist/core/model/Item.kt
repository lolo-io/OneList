package com.lolo.io.onelist.core.model

import java.io.Serializable

data class Item(
    var title: String = "",
    var comment: String = "",
    var done: Boolean = false,
    var commentDisplayed: Boolean = false,
    var id: Long = System.currentTimeMillis(),

) : Serializable