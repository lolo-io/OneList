package com.lolo.io.onelist.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.*
import com.lolo.io.onelist.model.ItemList
import com.lolo.io.onelist.MainActivity
import com.lolo.io.onelist.R
import com.lolo.io.onelist.updates.appContext
import com.lolo.io.onelist.util.*
import kotlinx.android.synthetic.main.dialog_edit_list.view.*
import kotlin.math.abs

@SuppressLint("InflateParams")
fun editListDialog(activity: MainActivity, list: ItemList = ItemList(), onPositiveClicked: (list: ItemList) -> Any?): AlertDialog {

    val isNewList = list.title.isEmpty()

    val originalPath = list.path
    var pathChanged = false

    var treeUri: Uri? = null

    if (isNewList && activity.persistence.defaultPath.isNotEmpty()) { // new list / custom path
        treeUri = activity.persistence.defaultPath.toUri
        pathChanged = true
    }

    val view = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_list, null).apply {
        listTitle.setText(list.title)
        listTitle.setSelection(list.title.length)
        listTitle.requestFocus()

        listTitle.afterTextChanged { text ->
            list.title = text
            if (pathChanged && list.notCustomPath) {
                list.path = list.getNewPath(text)
                listStorageButton.text = list.path
            }
        }

        optionsLayout.visibility = View.GONE
        moreOptionsLayout.setOnClickListener {
            moreOptionsCursor.apply { rotation = abs(rotation - 180f) }
            optionsLayout.visibility = if (moreOptionsCursor.rotation == 0f) View.GONE else View.VISIBLE
        }
    }

    val dialog = AlertDialog.Builder(activity).run {
        setView(view)
        create()
    }.apply {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        setCanceledOnTouchOutside(true)
        setOnCancelListener { list.path = originalPath }
    }

    view.apply {
        validateEditList.setOnClickListener {
            if (canValidate(listTitle)) {
                list.title = view.listTitle.text.toString()
                dialog.dismiss()

                treeUri?.let { uri ->
                    // Create a new list and save it on disk in the default path (can be changed by user)
                    Log.d("OneList", "Debugv Create File for new list: uri: " + uri.toString() + " - list filename: " + list.fileName)
                    if (Build.VERSION.SDK_INT >= 29) {
                        Log.d("OneList", "Debugv Create File for new list in custom folder: uri: " + uri)
                        val folder = DocumentFileCompat.fromFullPath(appContext, uri.toString()!!, requiresWriteAccess=true)
                        folder?.makeFile(appContext, list.fileName, "text/plain", mode=CreateMode.REPLACE)?.let {
                            list.path = it.getAbsolutePath(appContext) // should be equal to: "$uri/${list.fileName}"
                            Log.d("OneList", "Debugv Create File list.path: " + list.path)
                        }
                    } else {
                        DocumentFile.fromTreeUri(activity, uri)?.createFile("text/x-json", list.fileName)?.let {
                            list.path = it.uri.toString()
                        }
                    }
                }

                onPositiveClicked(list)
                if (originalPath.isNotBlank() && originalPath != list.path) warningNewFile(activity)
            }
        }

        listStorageButton.apply {
            text = list.path.toUri?.path?.beautify()
                    ?: treeUri?.path?.beautify()
                    ?: list.path.takeIf { it.isNotBlank() }
                            ?:  context.getString(R.string.app_private_storage)
            setOnClickListener {
                storagePathDialog(activity) { path ->
                    treeUri = path.toUri
                    list.path = if (treeUri == null && path.isNotBlank()) "$it/${list.fileName}" else ""
                    listStorageButton.text = treeUri?.path?.beautify()
                            ?: list.path.takeIf { it.isNotBlank() }
                                    ?: context.getString(R.string.app_private_storage)
                    pathChanged = true
                }
            }
        }

        listImportButton.apply {
            visibility = if (isNewList) View.VISIBLE else View.GONE
            setOnClickListener {
                Log.d("OneList", "Debugv before importList selectFile")
                selectFile(activity) {
                    Log.d("OneList", "Debugv after importList selectFile")
                    try {
                        Log.d("OneList", "Debugv before importList: ${it.toString()}")
                        val fpath = it.toString()
                        val imported = activity.persistence.importList(fpath).apply {
                            path = fpath
                        }
                        Log.d("OneList", "Debugv after return import file selected")
                        onPositiveClicked(imported)
                        Toast.makeText(activity, context.getString(R.string.list_added, imported.title), Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Log.d("OneList", "Debugv import file failed: " + e.stackTraceToString())
                        Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                    } finally {
                        dialog.dismiss()
                    }
                }
            }
        }

        cancelEditList.setOnClickListener { dialog.cancel() }

        listTitle.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) validateEditList.performClick()
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
