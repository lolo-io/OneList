package com.lolo.io.onelist.model

import android.net.Uri
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.annotations.SerializedName
import com.lolo.io.onelist.App
import com.lolo.io.onelist.R
import kotlinx.android.synthetic.main.fragment_one_list.*
import java.net.URI

data class ItemList(@SerializedName("title") var title: String = "", @SerializedName("items") val items: MutableList<Item> = arrayListOf(), @SerializedName("stableId") val stableId: Long = System.currentTimeMillis(), @Transient var path: String = "") {
    override fun toString(): String {
        return """
$title:

${items.joinToString("\n") { "$it" }}

${App.instance.getString(R.string.list_created_with_one1ist)}
${App.instance.getString(R.string.app_link)}
        """.trimIndent()
    }
}

fun ItemList.switchItemStatus(item: Item) {
    if(items.contains(item)) {
        item.done = !item.done
        val oldPosition = items.indexOf(item)
        val newPosition = when (item.done) {
            true -> items.size - 1
            else -> 0
        }
        items.removeAt(oldPosition)
        items.add(newPosition, item)
    }
}
