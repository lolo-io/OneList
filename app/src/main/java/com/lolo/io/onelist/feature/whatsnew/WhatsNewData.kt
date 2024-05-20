package com.lolo.io.onelist.feature.whatsnew

import androidx.compose.ui.graphics.vector.ImageVector

data class WhatsNewData(
    val title: String,
    val items: List<WhatsNewItem>
)


sealed class WhatsNewItem(
    val description: String? = null
) {
    class WhatsNewTitleItem private constructor (
        val title: String? = null,
        val iconRes: Int? = null,
        val imageVector: ImageVector? = null,
        description: String? = null,
    ) : WhatsNewItem(description) {


        constructor(
            title: String? = null,
            imageVector: ImageVector? = null,
            description: String? = null,
        ) : this(title = title, iconRes = null, imageVector = imageVector, description = description)

        constructor(
            title: String? = null,
            iconRes: Int? = null,
            description: String? = null,
        ) : this(title = title, iconRes = iconRes, imageVector = null, description = description)
    }

    class WhatsNewTextItem(description: String? = null) : WhatsNewItem(description)
}

