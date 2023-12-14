package com.lolo.io.onelist.feature.settings

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.lolo.io.onelist.R
import io.github.tonnyl.whatsnew.WhatsNew
import io.github.tonnyl.whatsnew.item.item
import io.github.tonnyl.whatsnew.item.whatsNew
import io.github.tonnyl.whatsnew.util.PresentationOption

fun showReleaseNote(activity: FragmentActivity) = whatsNew {
    backgroundColorResource = R.color.colorBackgroundPopup
    titleText = activity.getString(R.string.onelist_updated)
    titleColor = ContextCompat.getColor(activity, R.color.colorPrimaryDark)
    iconColor = ContextCompat.getColor(activity, R.color.colorPrimaryDark)
    buttonBackground = ContextCompat.getColor(activity, R.color.colorPrimaryDark)
    buttonText = activity.getString(R.string.continue_button)
    buttonTextColor = ContextCompat.getColor(activity, R.color.colorBackground)
    itemTitleColor = ContextCompat.getColor(activity, R.color.textColorPrimary)
    itemContentColor = ContextCompat.getColor(activity, R.color.textColorSecondary)
    presentationOption = PresentationOption.ALWAYS

    item {
        title = activity.getString(R.string.external_folder_title)
        content = activity.getString(R.string.external_folder_content)
        imageRes = R.drawable.ic_save_whatsnew_24dp
    }

}.apply {
    setStyle(DialogFragment.STYLE_NO_TITLE, 0)
    show(activity.supportFragmentManager, WhatsNew.TAG)
}
