package com.lolo.io.onelist.feature.lists.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.lolo.io.onelist.R
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.ui.util.*
import com.lolo.io.onelist.databinding.DialogEditListBinding

@SuppressLint("InflateParams")
fun editListDialog(
    context: Context,
    list: ItemList = ItemList(),
    onPositiveClicked: (list: ItemList) -> Any?
): AlertDialog {

    val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_list, null)
    val binding = DialogEditListBinding.bind(view).apply {
        listTitle.setText(list.title)
        listTitle.setSelection(list.title.length)
        listTitle.requestFocus()

        listTitle.afterTextChanged { text ->
            list.title = text
        }

    }

    val dialog = AlertDialog.Builder(context).run {
        setView(view)
        create()
    }.apply {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        setCanceledOnTouchOutside(true)
    }

    view.apply {
        binding.validateEditList.setOnClickListener {
            if (canValidate(binding.listTitle)) {
                list.title = binding.listTitle.text.toString()
                dialog.dismiss()
                onPositiveClicked(list)
            }
        }

        binding.cancelEditList.setOnClickListener { dialog.cancel() }

        binding.listTitle.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) binding.validateEditList.performClick()
            true
        }
    }

    return dialog
}

private fun canValidate(editText: EditText): Boolean =
    if (editText.text.toString().isNotEmpty()) {
        true
    } else {
        editText.shake()
        false
    }

private fun warningNewFile(context: Context) {
    Toast.makeText(context, context.getString(R.string.warning_new_file), Toast.LENGTH_LONG).show()
}
