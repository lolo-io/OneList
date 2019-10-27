package com.lolo.io.onelist.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.lolo.io.onelist.model.ItemList
import com.lolo.io.onelist.R
import kotlinx.android.synthetic.main.dialog_delete_list.view.*

const val ACTION_DELETE = 0x1
const val ACTION_CLEAR = 0x2
const val ACTION_RM_FILE = 0x4

@SuppressLint("InflateParams")
fun deleteListDialog(activity: Activity, itemList: ItemList, onPositiveClicked: (action: Int) -> Any?): AlertDialog {
    val view = LayoutInflater.from(activity).inflate(R.layout.dialog_delete_list, null).apply {
        deleteListTitle.text = itemList.title
    }
    val dialog = AlertDialog.Builder(activity).run {
        setView(view)
        create()
    }.apply {
        setCanceledOnTouchOutside(false)
    }

    var action = ACTION_RM_FILE
    if (itemList.path.isBlank()) view.deleteFileCb.apply {
        visibility = View.GONE
    } else {
        view.deleteFileCb.setOnCheckedChangeListener { _, b -> action = if (b) action or ACTION_RM_FILE else action and (action xor ACTION_RM_FILE) }
    }

    view.apply {
        validateDeleteList.setOnClickListener { action = action or ACTION_DELETE; onPositiveClicked(action); dialog.dismiss() }
        clearList.setOnClickListener { action = action or ACTION_CLEAR; onPositiveClicked(action); dialog.dismiss() }
        cancelDeleteList.setOnClickListener { dialog.dismiss() }
    }

    return dialog
}
