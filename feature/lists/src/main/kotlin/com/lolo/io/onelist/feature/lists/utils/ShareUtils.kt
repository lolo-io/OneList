package com.lolo.io.onelist.feature.lists.utils

import android.content.Context
import android.content.Intent
import com.lolo.io.onelist.feature.lists.R

fun shareList(context: Context, list: com.lolo.io.onelist.core.model.ItemList) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, list.toStringForShare(context))
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

private fun com.lolo.io.onelist.core.model.ItemList.toStringForShare(context: Context): String {
    return """
$title:

${items.joinToString("\n") { it.toStringForShare() }}

${context.getString(R.string.list_created_with_onelist)}
${context.getString(R.string.app_link)}
        """.trimIndent()
}

private fun com.lolo.io.onelist.core.model.Item.toStringForShare(): String {
    var string = "${if (done) "x" else "-"} $title"
    if (comment.isNotBlank()) string += "\n    ${comment.replace("\n", "\n    ")}"
    return string
}