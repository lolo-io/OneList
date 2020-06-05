package com.lolo.io.onelist.util

import android.animation.ObjectAnimator
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import com.lolo.io.onelist.App
import com.lolo.io.onelist.R

const val BUTTON_ANIM_DURATION: Long = 150

fun View.shake() {
    val shake = AnimationUtils.loadAnimation(App.instance.mainContext, R.anim.shake)
    startAnimation(shake)
}

fun View.animHideFlip(duration: Long = BUTTON_ANIM_DURATION, startDelay : Long = 0) {
    val hide = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f)
    hide.startDelay = startDelay
    val hideFlip = ObjectAnimator.ofFloat(this, "rotationY", 0f, 90f)
    hideFlip.duration = duration
    hideFlip.start()
    hide.start()
}

fun View.animShowFlip(duration: Long = BUTTON_ANIM_DURATION, startDelay : Long = 0) {
    val show = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f)
    show.startDelay = startDelay
    show.startDelay = duration
    val showFlip = ObjectAnimator.ofFloat(this, "rotationY", -90f, 0f)
    showFlip.duration = duration
    showFlip.startDelay = duration
    showFlip.start()
    show.start()
}

fun View.animTranslation(startX : Float = 0f, stopX : Float = 0f, duration: Long = BUTTON_ANIM_DURATION, startDelay : Long = 0) {
    val translate = ObjectAnimator.ofFloat(this, "translationX", startX, stopX)
    translate.duration = duration
    translate.startDelay = startDelay
    translate.start()
}

fun View.flipX() {
    val flipX = ObjectAnimator.ofFloat(this, "rotationX", this.rotationX, 180f - this.rotationX)
    flipX.duration = BUTTON_ANIM_DURATION
    flipX.start()
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}
