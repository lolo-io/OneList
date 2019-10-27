package com.lolo.io.onelist.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.lolo.io.onelist.model.Item
import com.lolo.io.onelist.R
import com.lolo.io.onelist.util.shake
import kotlinx.android.synthetic.main.dialog_edit_item.*
import kotlinx.android.synthetic.main.dialog_edit_item.view.*

@SuppressLint("InflateParams")
fun editItemDialog(activity: Activity, item: Item, onDoneEditing: (_: Item) -> Any?): AlertDialog {
    val view = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_item, null).apply {
        item_title.setText(item.title)
        item_title.setSelection(item_title.text.length)
        item_comment.setText(item.comment)
        item_title.requestFocus()
    }

    val dialog = AlertDialog.Builder(activity).run {
        setView(view)
        create()
    }.apply {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        setCanceledOnTouchOutside(true)
        setOnCancelListener {
            onDoneEditing(item)
        }
    }

    view.apply {
        validateEdit.setOnClickListener {
            if (view.item_title.text.toString().isEmpty()) {
                dialog.item_title.shake()
            } else {
                val newItem = Item(view.item_title.text.toString(), view.item_comment.text.toString(), item.done, item.commentDisplayed)
                onDoneEditing(newItem)
                dialog.dismiss()
            }
        }
        cancelEdit.setOnClickListener {
            onDoneEditing(item)
            dialog.dismiss()
        }
    }
    return dialog
}