package com.lolo.io.onelist.feature.whatsnew

import android.content.Context
import com.lolo.io.onelist.R

internal fun currentReleaseWhatsNewData(context: Context) = WhatsNewData(
    title = context.getString(R.string.onelist_updated),
    items = listOf(
        WhatsNewItem.WhatsNewTitleItem(
            title = context.getString(R.string.external_folder_title),
            iconRes = R.drawable.ic_settings_save_24dp,
        ),

        WhatsNewItem.WhatsNewTextItem(
            description = context.getString(R.string.external_folder_content)
        )
    )
)