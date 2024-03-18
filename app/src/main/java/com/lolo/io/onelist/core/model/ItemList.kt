package com.lolo.io.onelist.core.model

import android.net.Uri
import com.google.gson.annotations.SerializedName
import java.io.Serializable
data class ItemList(
    var title: String = "",
    @Transient  var position: Int = 0,
    var items: List<Item> = listOf(),
    @Transient var uri: Uri? = null,
    @Transient var id: Long = 0L,
) : Serializable {
    companion object
}


val ItemList.Companion.preview
    get() = ItemList(
        title = "Preview Item List",
        position = 1,
        items = (0 .. 3).map { Item.preview.copy(title = "Preview Item $it") }.toMutableList(),
        uri = null,
        id = Math.random().toLong()
    )

fun ItemList.Companion.previewMany(n: Int) = (0 .. n).map {
    ItemList.preview.copy(
        title = "Preview Item List $it",
        position = it+1,
    )
}