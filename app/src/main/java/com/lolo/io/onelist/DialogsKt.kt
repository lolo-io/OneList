package com.lolo.io.onelist

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.dialog_delete_list.view.*
import kotlinx.android.synthetic.main.dialog_edit_item.*
import kotlinx.android.synthetic.main.dialog_edit_item.view.*
import kotlinx.android.synthetic.main.dialog_new_list.view.*

class DialogsKt(private val context: Context) {

    companion object {
        const val ACTION_DELETE = 1
        const val ACTION_CLEAR = 2
    }

    @SuppressLint("InflateParams")
    fun promptDeleteList(itemList: ItemList, onPositiveClicked: (action: Int) -> Any?): AlertDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_delete_list, null)
        view.deleteListTitle.text = itemList.title
        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        val dialog = builder.create()
        view.validateDeleteList.setOnClickListener{ onPositiveClicked(ACTION_DELETE); dialog.dismiss() }
        view.clearList.setOnClickListener{ onPositiveClicked(ACTION_CLEAR); dialog.dismiss() }
        view.cancelDeleteList.setOnClickListener { dialog.dismiss() }
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    @SuppressLint("InflateParams")
    fun promptAddOrEditList(originalTitle: String = "", onPositiveClicked: (listTitle: String) -> Any?): AlertDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_new_list, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        view.listTitle.setText(originalTitle)
        view.listTitle.setSelection(view.listTitle.text.length)
        view.listTitle.requestFocus()
        view.validateEditList.setOnClickListener { onClickOkEditList(view, onPositiveClicked, dialog) }
        view.cancelEditList.setOnClickListener { dialog.dismiss() }
        view.listTitle.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) onClickOkEditList(view, onPositiveClicked, dialog)
            true
        }
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    private fun onClickOkEditList(view: View, onPositiveClicked: (listTitle: String) -> Any?, dialog: AlertDialog) {
        if (view.listTitle.text.toString().isEmpty()) {
            view.listTitle.shake()
        } else {
            onPositiveClicked(view.listTitle.text.toString())
            dialog.dismiss()
        }
    }

    @SuppressLint("InflateParams")
    fun promptEditItem(item: Item, onDoneEditing: (_: Item) -> Any?): AlertDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_item, null)
        view.item_title.setText(item.title)
        view.item_title.setSelection(view.item_title.text.length)
        view.item_comment.setText(item.comment)
        view.item_title.requestFocus()
        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        view.validateEdit.setOnClickListener {
            if (view.item_title.text.toString().isEmpty()) {
                dialog.item_title.shake()
            } else {
                val newItem = Item(view.item_title.text.toString(), view.item_comment.text.toString(), item.done, item.commentDisplayed)
                onDoneEditing(newItem)
                dialog.dismiss()
            }
        }
        view.cancelEdit.setOnClickListener {
            onDoneEditing(item)
            dialog.dismiss()
        }
        dialog.setOnCancelListener {
            onDoneEditing(item)
        }
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }
}