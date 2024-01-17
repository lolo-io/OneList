package com.lolo.io.onelist.core.model

import android.net.Uri
import com.google.gson.annotations.SerializedName
import java.io.Serializable
data class ItemList(
    var title: String = "",
    @Transient  var position: Int = 0,
    val items: MutableList<Item> = arrayListOf(),
    @Transient var uri: Uri? = null,
    @Transient var id: Long = 0L,
) : Serializable




