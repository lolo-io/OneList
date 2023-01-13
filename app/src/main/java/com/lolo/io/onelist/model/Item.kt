package com.lolo.io.onelist.model

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.gson.annotations.SerializedName
import com.lolo.io.onelist.updates.appContext

data class Item(@SerializedName("title") var title: String = "", @SerializedName("comment") var comment: String = "", @SerializedName("done") var done: Boolean = false, @SerializedName("commentDisplayed") var commentDisplayed: Boolean = false, @SerializedName("stableId") val stableId: Long = System.currentTimeMillis(), @Transient var markdown: Boolean = false) {
    @SuppressLint("RestrictedApi")
    override fun toString(): String {
        var string =
                if (markdown) {
                    "${if (done) "- [x]" else "- [ ]"} $title"
                } else {
                    "${if (done) "x" else "o"} $title"
                }
        if (comment.isNotBlank()) string += "\n    ${comment.replace("\n", "\n    ")}"
        return string
    }
}