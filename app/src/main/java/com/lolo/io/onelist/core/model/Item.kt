package com.lolo.io.onelist.core.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import java.io.Serializable
import kotlin.math.roundToLong

data class Item(
    var title: String = "",
    var comment: String = "",
    var done: Boolean = false,
    var commentDisplayed: Boolean = false,
    var id: Long = System.currentTimeMillis(),

    ) : Serializable {
    companion object
}

val Item.Companion.preview
    get() = Item(
        title = "Preview Item",
        comment = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut eu felis non enim ornare placerat at quis lorem. Nam vel ligula ligula. Aenean convallis magna eu lacus cursus, id tempor ex malesuada.",
        done = false,
        commentDisplayed = false,
        id = Math.random().roundToLong()
    )


fun Item.Companion.previewMany(n: Int) = (0 .. n).map {
    Item.preview.copy(
        title = "Preview Item $it",
        id = it.toLong(),
    )
}