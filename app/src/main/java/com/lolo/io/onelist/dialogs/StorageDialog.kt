package com.lolo.io.onelist.dialogs

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.documentfile.provider.DocumentFile
import androidx.preference.PreferenceManager
import com.codekidlabs.storagechooser.Content
import com.codekidlabs.storagechooser.StorageChooser
import com.lolo.io.onelist.App
import com.lolo.io.onelist.MainActivity
import com.lolo.io.onelist.R
import kotlinx.android.synthetic.main.dialog_list_path.view.*
import com.anggrayudi.storage.file.*
import com.lolo.io.onelist.updates.appContext
import com.lolo.io.onelist.util.*


@SuppressLint("InflateParams")
fun defaultPathDialog(activity: MainActivity, onPathChosen: (String) -> Unit) {
    val view = LayoutInflater.from(activity).inflate(R.layout.dialog_list_path, null)
    view.listPathTitle.text = activity.getString(R.string.default_storage_folder)
    displayDialog(view, activity, onPathChosen)
}

@SuppressLint("InflateParams")
fun storagePathDialog(activity: MainActivity, onPathChosen: (String) -> Unit) {
    val view = LayoutInflater.from(activity).inflate(R.layout.dialog_list_path, null)
    displayDialog(view, activity, onPathChosen)
}

fun displayDialog(view: View, activity: MainActivity, onPathChosen: (String) -> Unit) {

    val dialog = AlertDialog.Builder(activity).run {
        setView(view)
        create()
    }.apply {
        setCanceledOnTouchOutside(true)
        show()
    }

    view.apply {
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
    // Folder picker, allows to store listst in an external folder, different than the app's folder
    // so that a file syncing app such as SyncThing can be used such
    withStoragePermission(activity) {
        // For Android >= 10, use scoped storage via SimpleStorage library
        if (Build.VERSION.SDK_INT >= 29) {
            // Register callback when a folder is picked (this is mostly unnecessary since we register a different callback with SimpleStorage)
            activity.onPathChosenActivityResult = onPathChosen
            Log.d("OneList", "Debugv Before SimpleStorageHelper callback func def")
            // Register a callback with SimpleStorage when a folder is picked
            activity.storageHelper.onFolderSelected = { _, root -> // could also use simpleStorageHelper.onStorageAccessGranted()
                Log.d("OneList", "Debugv Success Folder Pick! Now saving...")
                // Get absolute path to folder
                val fpath = root.getAbsolutePath(appContext)
                /*
                // Open preferences to save folder to path. Works but commented because unnecessary for working app, was used for debugging
                // Alternatively, since SimpleStorage v1.5.4, we do not even need to store folder path, we can get the list of granted paths.
                val preferences = PreferenceManager.getDefaultSharedPreferences(appContext)
                preferences.edit().putString("defaultPathPref", uri).apply()
                 */
                // Update persistent.defaultPath with the folder path
                Log.d("OneList", "Debugv Folder Pick New File Creation")
                activity.onPathChosenActivityResult(fpath)
                activity.onPathChosenActivityResult = { }
            }
            // Open folder picker via SimpleStorage, this will request the necessary scoped storage permission
            // Note that even though we get permissions, we need to only write DocumentFile files, not MediaStore files, because the latter are not meant to be reopened in the future so we can lose permission at anytime once they are written once, see: https://github.com/anggrayudi/SimpleStorage/issues/103
            Log.d("OneList", "Debugv Get Storage Access permission")
            activity.storageHelper.openFolderPicker(  // We could also use simpleStorageHelper.requestStorageAccess()
                    initialPath = FileFullPath(activity, StorageId.PRIMARY, "OneList"), // SimpleStorage.externalStoragePath
                    //expectedStorageType = StorageType.EXTERNAL,
                    //expectedBasePath = "OneList"
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.onPathChosenActivityResult = onPathChosen
            activity.startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
            }, REQUEST_CODE_OPEN_DOCUMENT_TREE)
        } else {
            @Suppress("DEPRECATION")
            StorageChooser.Builder()
                    .withActivity(activity)
                    .withContent(storageChooserLocales)
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

fun selectFile(activity: MainActivity, onPathChosen: (String) -> Any?) {
    // File picker
    withStoragePermission(activity) {
        // If Android >= 10, use SimpleStorage to handle scoped storage permissions
        if (Build.VERSION.SDK_INT >= 29) {
            // Register a callback when a file is selected
            activity.storageHelper.onFileSelected = { _, files ->
                // pick the first file in the returned array, because the file picker allows to select multiple files (although we here limit to one)
                val file = files.first()
                Log.d("OneList", "Debugv file selected: ${file.fullName}")
                // get absolute path to file and save it
                onPathChosen(file.getAbsolutePath(activity))
            }
            // Open file picker via SimpleStorage to get permission to read/write it
            activity.storageHelper.openFilePicker(
                    allowMultiple = false,
                    initialPath = FileFullPath(activity, StorageId.PRIMARY, "Download"), // SimpleStorage.externalStoragePath
            )
        // File picker for older Android versions
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.onPathChosenActivityResult = onPathChosen
            activity.startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type = "*/*" }, REQUEST_CODE_OPEN_DOCUMENT)
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
                            if (it.endsWith(".1list.json")) {
                                onPathChosen(it)
                            } else {
                                Toast.makeText(activity, activity.getString(R.string.not_a_1list_file), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
        }
    }
}

val storageChooserLocales = Content().apply {
    selectLabel = App.instance.getString(R.string.storage_chooser_select_label)
    createLabel = App.instance.getString(R.string.storage_chooser_create_label)
    newFolderLabel = App.instance.getString(R.string.storage_chooser_new_folder_label)
    cancelLabel = App.instance.getString(R.string.storage_chooser_cancel_label)
    overviewHeading = App.instance.getString(R.string.storage_chooser_overview_heading)
    internalStorageText = App.instance.getString(R.string.storage_chooser_internal_storage_text)
    freeSpaceText = App.instance.getString(R.string.storage_chooser_free_space_text)
    folderCreatedToastText = App.instance.getString(R.string.storage_chooser_folder_created_toast_text)
    folderErrorToastText = App.instance.getString(R.string.storage_chooser_folder_error_toast_text)
    textfieldHintText = App.instance.getString(R.string.storage_chooser_text_field_hint_text)
    textfieldErrorText = App.instance.getString(R.string.storage_chooser_text_field_error_text)
}