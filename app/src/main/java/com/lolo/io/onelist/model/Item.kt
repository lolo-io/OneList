package com.lolo.io.onelist.model

import com.google.gson.annotations.SerializedName

data class Item(@SerializedName("title") var title: String = "", @SerializedName("comment") var comment: String = "", @SerializedName("done") var done: Boolean = false, @SerializedName("commentDisplayed") var commentDisplayed: Boolean = false, @SerializedName("stableId") val stableId: Long = System.currentTimeMillis()) {
    override fun toString(): String {
        var string = "${if (done) "x" else "o"} $title"
        if (comment.isNotBlank()) string += "\n    ${comment.replace("\n", "\n    ")}"
        return string
    }
}