package com.lolo.io.onelist.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.lolo.io.onelist.R
import com.lolo.io.onelist.databinding.DialogEditItemBinding
import com.lolo.io.onelist.model.Item
import com.lolo.io.onelist.util.shake

@SuppressLint("InflateParams")
fun editItemDialog(activity: Activity, item: Item, onDoneEditing: (_: Item) -> Any?): AlertDialog {
    val view = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_item, null)
    val binding = DialogEditItemBinding.bind(view)
    binding.itemTitle.setText(item.title)
    binding.itemTitle.setSelection(binding.itemTitle.text.length)
    binding.itemComment.setText(item.comment)
    binding.itemTitle.requestFocus()

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
        binding.validateEdit.setOnClickListener {
            if (binding.itemTitle.text.toString().isEmpty()) {
                binding.itemTitle.shake()
            } else {
                val newItem = item.copy(
                    title = binding.itemTitle.text.toString(),
                    comment = binding.itemComment.text.toString(),
                )
                onDoneEditing(newItem)
                dialog.dismiss()
            }
        }
        binding.cancelEdit.setOnClickListener {
            onDoneEditing(item)
            dialog.dismiss()
        }
    }
    return dialog
}