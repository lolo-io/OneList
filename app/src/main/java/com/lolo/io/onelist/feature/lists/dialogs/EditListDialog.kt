package com.lolo.io.onelist.feature.lists.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.lolo.io.onelist.R
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.ui.util.*
import com.lolo.io.onelist.databinding.DialogEditListBinding
import kotlin.math.abs

@SuppressLint("InflateParams")
fun editListDialog(
    context: Context,
    list: ItemList = ItemList(),
    onPositiveClicked: (list: ItemList) -> Any?
): AlertDialog {

    val isNewList = list.title.isEmpty()

    val originalPath = list.path
    var pathChanged = false

    var treeUri: Uri? = null
    /* todo change soon
        if (isNewList && activity.persistence.backupUri.isNotNullOrEmpty()) { // new list / custom path
            treeUri = activity.persistence.backupUri.toUri
            pathChanged = true
        }
    */
    val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_list, null)
    val binding = DialogEditListBinding.bind(view).apply {
        listTitle.setText(list.title)
        listTitle.setSelection(list.title.length)
        listTitle.requestFocus()

        listTitle.afterTextChanged { text ->
            list.title = text
            /* if (pathChanged && list.notCustomPath) {
                 //list.uri = list.getNewPath(text)
              //   listStorageButton.text = list.path
             }*/
        }

        optionsLayout.visibility = View.GONE
        moreOptionsLayout.setOnClickListener {
            moreOptionsCursor.apply { rotation = abs(rotation - 180f) }
            optionsLayout.visibility =
                if (moreOptionsCursor.rotation == 0f) View.GONE else View.VISIBLE
        }
    }

    val dialog = AlertDialog.Builder(context).run {
        setView(view)
        create()
    }.apply {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        setCanceledOnTouchOutside(true)
        setOnCancelListener { list.path = originalPath }
    }

    view.apply {
        binding.validateEditList.setOnClickListener {
            if (canValidate(binding.listTitle)) {
                list.title = binding.listTitle.text.toString()
                dialog.dismiss()

                /*    treeUri?.let { uri ->
                        DocumentFile.fromTreeUri(activity, uri)?.createFile("text/x-json", list.fileName)?.let {
                            list.path = it.uri.toString()
                        }
                    }*/

                onPositiveClicked(list)
                //   if (originalPath.isNotBlank() && originalPath != list.path) warningNewFile(activity)
            }
        }
        /*
                binding.listStorageButton.apply {
                    text = list.path.toUri?.path?.beautify()
                            ?: treeUri?.path?.beautify()
                            ?: list.path.takeIf { it.isNotBlank() }
                                    ?:  context.getString(R.string.app_private_storage)
                    setOnClickListener {
                        storagePathDialog(activity) { path ->
                            treeUri = path.toUri
                            list.path = if (treeUri == null && path.isNotBlank()) "$it/${list.fileName}" else ""
                            binding.listStorageButton.text = treeUri?.path?.beautify()
                                    ?: list.path.takeIf { it.isNotBlank() }
                                            ?: context.getString(R.string.app_private_storage)
                            pathChanged = true
                        }
                    }
                }

        binding.listImportButton.apply {
            visibility = if (isNewList) View.VISIBLE else View.GONE
            setOnClickListener {
                selectFile(activity) {
                    try {
                        val imported = activity.persistence.importList(it).apply { path = it }
                        onPositiveClicked(imported)
                        Toast.makeText(
                            activity,
                            context.getString(R.string.list_added, imported.title),
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                    } finally {
                        dialog.dismiss()
                    }
                }
            }
        }


         */

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
