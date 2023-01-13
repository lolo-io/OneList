package com.lolo.io.onelist.model

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.material.internal.ContextUtils
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.gson.annotations.SerializedName
import com.lolo.io.onelist.App
import com.lolo.io.onelist.R
import com.lolo.io.onelist.updates.appContext
import java.net.URI

data class ItemList(@SerializedName("title") var title: String = "", @SerializedName("items") val items: MutableList<Item> = arrayListOf(), @SerializedName("stableId") val stableId: Long = System.currentTimeMillis(), @Transient var path: String = "", @Transient var markdown: Boolean = false) {

    override fun toString(): String {
        return toStringNoAd() + toStringOnlyAd()
    }

    fun toStringNoAd(): String {
        // Update markdown format of every items if we use markdown for this ItemList
        for (i in items) {
            i.markdown = markdown
        }
        return """
${if (markdown) "## $title" else "$title:"}

${items.joinToString("\n") { "$it" }}
        """.trimIndent()
    }

    fun toStringOnlyAd(): String {
        return """

${App.instance.getString(R.string.list_created_with_one1ist)}
${App.instance.getString(R.string.app_link)}
        """.trimIndent()
    }

}
