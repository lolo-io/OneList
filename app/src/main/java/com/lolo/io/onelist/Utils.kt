package com.lolo.io.onelist

import android.content.Context
import java.io.IOException

fun loadJSONFromAsset(context: Context, filename: String): String {
    val json: String?
    try {
        val `is` = context.assets.open(filename)
        val size = `is`.available()
        val buffer = ByteArray(size)
        `is`.read(buffer)
        `is`.close()
        json = String(buffer, Charsets.UTF_8)
    } catch (ex: IOException) {
        ex.printStackTrace()
        return ""
    }
    return json
}

fun dpToPx(dp: Int): Int {
    val density = App.instance.resources.displayMetrics.density
    return Math.round(dp.toFloat() * density)
}