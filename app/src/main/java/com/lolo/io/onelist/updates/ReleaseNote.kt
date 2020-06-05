package com.lolo.io.onelist.updates

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.lolo.io.onelist.App
import com.lolo.io.onelist.R
import io.github.tonnyl.whatsnew.WhatsNew
import io.github.tonnyl.whatsnew.item.item
import io.github.tonnyl.whatsnew.item.whatsNew
import io.github.tonnyl.whatsnew.util.PresentationOption

object ReleaseNote {

    val releasesNotes = linkedMapOf(
            "1.1" to { maj1Min1() },
            "1.2" to { maj1Min2() },
            "1.3" to { maj1Min3() }
    )

    private fun maj1Min1() =
            whatsNew {
                titleText = appContext.getString(R.string.onelist_updated)
                titleColor = ContextCompat.getColor(appContext, R.color.colorPrimaryDark)
                iconColor = ContextCompat.getColor(appContext, R.color.colorPrimaryDark)
                buttonBackground = ContextCompat.getColor(appContext, R.color.colorPrimaryDark)
                buttonText = appContext.getString(R.string.continue_button)
                buttonTextColor = ContextCompat.getColor(appContext, R.color.white)
                presentationOption = PresentationOption.ALWAYS

                item {
                    title = appContext.getString(R.string.external_folder_title)
                    content = appContext.getString(R.string.external_folder_content)
                    imageRes = R.drawable.ic_save_whatsnew_24dp
                }
                item {
                    title = appContext.getString(R.string.import_title)
                    content = appContext.getString(R.string.import_content)
                    imageRes = R.drawable.ic_add_circle_outline_whatsnew_24dp
                }
                item {
                    title = appContext.getString(R.string.pull_title)
                    content = appContext.getString(R.string.pull_content)
                    imageRes = R.drawable.ic_refresh_whatsnew_24dp
                }
                item {
                    title = appContext.getString(R.string.settings_title)
                    content = appContext.getString(R.string.settings_content)
                    imageRes = R.drawable.ic_settings_whatsnew_24dp
                }
                item {
                    title = appContext.getString(R.string.share_title)
                    content = appContext.getString(R.string.share_content)
                    imageRes = R.drawable.ic_share_whatsnew_24dp
                }
                item {
                    content = appContext.getString(R.string.see_again_in_settings)
                }
            }

    private fun maj1Min2() = whatsNew {
                backgroundColorResource =  R.color.colorBackgroundPopup
                titleText = appContext.getString(R.string.onelist_updated)
                titleColor = ContextCompat.getColor(appContext, R.color.colorPrimaryDark)
                iconColor = ContextCompat.getColor(appContext, R.color.colorPrimaryDark)
                buttonBackground = ContextCompat.getColor(appContext, R.color.colorPrimaryDark)
                buttonText = appContext.getString(R.string.continue_button)
                buttonTextColor = ContextCompat.getColor(appContext, R.color.white)
                itemTitleColor = ContextCompat.getColor(appContext, R.color.textColorPrimary)
                itemContentColor = ContextCompat.getColor(appContext, R.color.textColorSecondary)
                presentationOption = PresentationOption.ALWAYS

                item {
                    title = appContext.getString(R.string.external_folder_title_1_2)
                    content = appContext.getString(R.string.external_folder_content_1_2)
                    imageRes = R.drawable.ic_save_whatsnew_24dp
                }

                item {
                    content = appContext.getString(R.string.see_again_in_settings)
                }
            }

    private fun maj1Min3() = whatsNew {
        backgroundColorResource =  R.color.colorBackgroundPopup
        titleText = appContext.getString(R.string.onelist_updated)
        titleColor = ContextCompat.getColor(appContext, R.color.colorPrimaryDark)
        iconColor = ContextCompat.getColor(appContext, R.color.colorPrimaryDark)
        buttonBackground = ContextCompat.getColor(appContext, R.color.colorPrimaryDark)
        buttonText = appContext.getString(R.string.continue_button)
        buttonTextColor = ContextCompat.getColor(appContext, R.color.white)
        itemTitleColor = ContextCompat.getColor(appContext, R.color.textColorPrimary)
        itemContentColor = ContextCompat.getColor(appContext, R.color.textColorSecondary)
        presentationOption = PresentationOption.ALWAYS

        item {
            title = appContext.getString(R.string.dark_theme_title_1_4)
            content = appContext.getString(R.string.dark_theme_content_1_4)
            imageRes = R.drawable.ic_color_lens_whatsnew_24dp
        }

        item {
            content = appContext.getString(R.string.see_again_in_settings)
        }
    }
}

fun WhatsNew.show(activity: AppCompatActivity) {
    setStyle(DialogFragment.STYLE_NO_TITLE, 0)
    show(activity.supportFragmentManager, WhatsNew.TAG)
}

val appContext
    get() = App.instance.mainContext