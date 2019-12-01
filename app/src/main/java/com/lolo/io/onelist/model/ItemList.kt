package com.lolo.io.onelist.model

import android.net.Uri
import com.google.gson.annotations.SerializedName
import com.lolo.io.onelist.App
import com.lolo.io.onelist.R
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
