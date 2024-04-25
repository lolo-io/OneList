package com.lolo.io.onelist.feature.whatsnew

import android.content.Context

internal fun currentReleaseWhatsNewData(context: Context) = WhatsNewData(
    title = context.getString(R.string.whatsnew_title),
    items = listOf(
        WhatsNewItem.WhatsNewTitleItem(
            title = context.getString(R.string.whatsnew_first_title),
            description = context.getString(R.string.whatsnew_first_desc),
            iconRes = R.drawable.ic_clear_all_24dp,
        ),

        WhatsNewItem.WhatsNewTitleItem(
            title = context.getString(R.string.whatsnew_second_title),
            description = context.getString(R.string.whatsnew_second_desc),
            iconRes = R.drawable.ic_settings_color_palette_24dp,
        ),

        WhatsNewItem.WhatsNewTitleItem(
            title = context.getString(R.string.whatsnew_third_title),
            description = context.getString(R.string.whatsnew_third_desc),
            iconRes = R.drawable.ic_settings_color_palette_24dp,
        ),

    )
)