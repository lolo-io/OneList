package com.lolo.io.onelist.core.model

import android.content.Context
import android.net.Uri
import com.lolo.io.onelist.R
import java.io.Serializable

data class ItemList(
    var title: String = "",
    var position: Int = 0,
    val items: MutableList<Item> = arrayListOf(),
    @Transient var path: Uri? = null,
    @Transient var uri: Uri? = null,
    var id: Long = 0L,
) : Serializable




