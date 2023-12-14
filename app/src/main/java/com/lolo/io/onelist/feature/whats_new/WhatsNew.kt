package com.lolo.io.onelist.feature.whats_new

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.lolo.io.onelist.App
import com.lolo.io.onelist.R
import io.github.tonnyl.whatsnew.WhatsNew
import io.github.tonnyl.whatsnew.item.item
import io.github.tonnyl.whatsnew.item.whatsNew
import io.github.tonnyl.whatsnew.util.PresentationOption

private fun shoReleaseNote(context: Context) = whatsNew {
    backgroundColorResource = R.color.colorBackgroundPopup
    titleText = context.getString(R.string.onelist_updated)
    titleColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    iconColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    buttonBackground = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    buttonText = context.getString(R.string.continue_button)
    buttonTextColor = ContextCompat.getColor(context, R.color.colorBackground)
    itemTitleColor = ContextCompat.getColor(context, R.color.textColorPrimary)
    itemContentColor = ContextCompat.getColor(context, R.color.textColorSecondary)
    presentationOption = PresentationOption.ALWAYS
}.apply {
    setStyle(DialogFragment.STYLE_NO_TITLE, 0)
    show(requireActivity().supportFragmentManager, WhatsNew.TAG)
}
