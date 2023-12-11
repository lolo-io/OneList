package com.lolo.io.onelist.feature.lists.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.codekidlabs.storagechooser.Content
import com.codekidlabs.storagechooser.StorageChooser
import com.lolo.io.onelist.App
import com.lolo.io.onelist.MainActivity
import com.lolo.io.onelist.R
import com.lolo.io.onelist.core.data.utils.withStoragePermission
import com.lolo.io.onelist.core.ui.REQUEST_CODE_OPEN_DOCUMENT_TREE
import com.lolo.io.onelist.databinding.DialogListPathBinding


@SuppressLint("InflateParams")
fun defaultPathDialog(activity: MainActivity, onPathChosen: (String) -> Unit) {
    val view = LayoutInflater.from(activity).inflate(R.layout.dialog_list_path, null)
    val binding = DialogListPathBinding.bind(view)
    binding.listPathTitle.text = activity.getString(R.string.default_storage_folder)
    displayDialog(binding, activity, onPathChosen)
}

@SuppressLint("InflateParams")
fun storagePathDialog(activity: MainActivity, onPathChosen: (String) -> Unit) {
    val view = LayoutInflater.from(activity).inflate(R.layout.dialog_list_path, null)
    val binding = DialogListPathBinding.bind(view)
    displayDialog(binding, activity, onPathChosen)
}

fun displayDialog(
    binding: DialogListPathBinding,
    activity: MainActivity,
    onPathChosen: (String) -> Unit
) {

    val dialog = AlertDialog.Builder(activity).run {
        setView(binding.root)
        create()
    }.apply {
        setCanceledOnTouchOutside(true)
        show()
    }

    binding.apply {
        appPrivateStorageButton.setOnClickListener {
            onPathChosen("")
            dialog.dismiss()
        }
        chooseFolderButton.setOnClickListener {
            selectDirectory(activity, onPathChosen)
            dialog.dismiss()
        }

        helpChangePath.setOnClickListener {
            AlertDialog.Builder(activity)
                .setMessage(R.string.changeFolderHelp)
                .setPositiveButton(activity.getString(R.string.ok_with_tick), null)
                .create().apply {
                    setCanceledOnTouchOutside(true)
                }.show()
        }
        cancelChangePath.setOnClickListener { dialog.dismiss() }

    }
}

fun selectDirectory(activity: MainActivity, onPathChosen: (String) -> Any?) {
    withStoragePermission(activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.onPathChosenActivityResult = onPathChosen
            activity.startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
            }, REQUEST_CODE_OPEN_DOCUMENT_TREE)
        } else {
            @Suppress("DEPRECATION")
            StorageChooser.Builder()
                .withActivity(activity)
                .withContent(storageChooserLocales(activity))
                .withFragmentManager(activity.fragmentManager) // activity.fragmentManager deprecated, but lib StorageChooser hasn't fully migrated to androidx yet.
                .withMemoryBar(true)
                .allowCustomPath(true)
                .allowAddFolder(true)
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .build()
                .apply {
                    show()
                    setOnSelectListener {
                        onPathChosen(it)
                    }
                }
        }
    }
}

fun selectFile(activity: MainActivity, onUri: (Uri) -> Any?) {
    activity.storageHelper.openFilePicker()
    activity.storageHelper.onFileSelected = { _, files ->
        onUri(files[0].uri)
    }

/*
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        activity.onPathChosenActivityResult = onPathChosen
        activity.startActivityForResult(
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type = "* / *" },
            REQUEST_CODE_OPEN_DOCUMENT
        )
    } else {
        @Suppress("DEPRECATION")
        StorageChooser.Builder()
            .withActivity(activity)
            .withFragmentManager(activity.fragmentManager) // activity.fragmentManager deprecated, but lib StorageChooser hasn't fully migrated to androidx yet.
            .withMemoryBar(true)
            .allowCustomPath(true)
            .setType(StorageChooser.FILE_PICKER)
            .build()
            .apply {
                show()
                setOnSelectListener {
                    if (it.endsWith(".1list")) {
                        onPathChosen(it)
                    } else {
                        Toast.makeText(
                            activity,
                            activity.getString(R.string.not_a_1list_file),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }
*/
}

fun storageChooserLocales(context: Context) = Content().apply {
    selectLabel = context.getString(R.string.storage_chooser_select_label)
    createLabel = context.getString(R.string.storage_chooser_create_label)
    newFolderLabel = context.getString(R.string.storage_chooser_new_folder_label)
    cancelLabel = context.getString(R.string.storage_chooser_cancel_label)
    overviewHeading = context.getString(R.string.storage_chooser_overview_heading)
    internalStorageText = context.getString(R.string.storage_chooser_internal_storage_text)
    freeSpaceText = context.getString(R.string.storage_chooser_free_space_text)
    folderCreatedToastText =
        context.getString(R.string.storage_chooser_folder_created_toast_text)
    folderErrorToastText = context.getString(R.string.storage_chooser_folder_error_toast_text)
    textfieldHintText = context.getString(R.string.storage_chooser_text_field_hint_text)
    textfieldErrorText = context.getString(R.string.storage_chooser_text_field_error_text)
}