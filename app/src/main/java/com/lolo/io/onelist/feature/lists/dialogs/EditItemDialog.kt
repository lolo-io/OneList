package com.lolo.io.onelist.feature.lists.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.lolo.io.onelist.R
import com.lolo.io.onelist.databinding.DialogEditItemBinding
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.ui.util.shake

@SuppressLint("InflateParams")
fun editItemDialog(context: Context, item: Item, onDoneEditing: (_: Item) -> Any?): AlertDialog {
    val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_item, null)
    val binding = DialogEditItemBinding.bind(view)
    binding.itemTitle.setText(item.title)
    binding.itemTitle.setSelection(binding.itemTitle.text.length)
    binding.itemComment.setText(item.comment)
    binding.itemTitle.requestFocus()

    val dialog = AlertDialog.Builder(context).run {
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