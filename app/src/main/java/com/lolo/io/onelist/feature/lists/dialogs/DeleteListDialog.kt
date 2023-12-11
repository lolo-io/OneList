package com.lolo.io.onelist.feature.lists.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.R
import com.lolo.io.onelist.databinding.DialogDeleteListBinding

const val ACTION_DELETE = 0x1
const val ACTION_CLEAR = 0x2
const val ACTION_RM_FILE = 0x4

@SuppressLint("InflateParams")
fun deleteListDialog(context: Context, itemList: ItemList, onPositiveClicked: (action: Int) -> Any?): AlertDialog {

    val view = LayoutInflater.from(context).inflate(R.layout.dialog_delete_list, null)
    val binding = DialogDeleteListBinding.bind(view)
    binding.deleteListTitle.text = itemList.title


    val dialog = AlertDialog.Builder(context).run {
        setView(view)
        create()
    }.apply {
        setCanceledOnTouchOutside(false)
    }

    var action = ACTION_RM_FILE
    if (itemList.uri == null) binding.deleteFileCb.apply {
        visibility = View.GONE
    } else {
        binding.deleteFileCb.setOnCheckedChangeListener { _, b -> action = if (b) action or ACTION_RM_FILE else action and (action xor ACTION_RM_FILE) }
    }

    view.apply {
        binding.validateDeleteList.setOnClickListener { action = action or ACTION_DELETE; onPositiveClicked(action); dialog.dismiss() }
        binding.clearList.setOnClickListener { action = action or ACTION_CLEAR; onPositiveClicked(action); dialog.dismiss() }
        binding.cancelDeleteList.setOnClickListener { dialog.dismiss() }
    }

    return dialog
}
