package com.lolo.io.onelist.feature.lists.utils

import android.content.Context
import com.lolo.io.onelist.R
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList

fun ItemList.toStringForShare(context: Context): String {
    return """
$title:

${items.joinToString("\n") { it.toStringForShare() }}

${context.getString(R.string.list_created_with_one1ist)}
${context.getString(R.string.app_link)}
        """.trimIndent()
}

fun Item.toStringForShare(): String {
    var string = "${if (done) "x" else "-"} $title"
    if (comment.isNotBlank()) string += "\n    ${comment.replace("\n", "\n    ")}"
    return string
}