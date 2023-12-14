package com.lolo.io.onelist.core.data.file_access

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.getAbsolutePath
import com.anggrayudi.storage.file.makeFile
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.lolo.io.onelist.R
import com.lolo.io.onelist.core.model.ItemList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException

class FileAccess(
    val app: Application,
) {
    private val coroutineIOScope = CoroutineScope(Dispatchers.IO)
    private val gson = Gson()


    private val Uri.canWrite
        get() =
            DocumentFileCompat.fromUri(app, this)?.canWrite() == true

    private fun Uri.isIntoBackupFolder(backupUri: Uri): Boolean =
        DocumentFileCompat.fromUri(app, this)?.getAbsolutePath(app)
            ?.startsWith(
                DocumentFileCompat.fromUri(app, backupUri)?.getAbsolutePath(app)
                    ?: throw IllegalArgumentException("Backup uri could not be parsed")
            ) == true

    private val Uri.fileExists
        get() =
            DocumentFileCompat.fromUri(app, this)?.exists() == true

    @Throws(
        FileNotFoundException::class,
        JsonSyntaxException::class,
        JsonIOException::class,
        SecurityException::class
    )
    suspend fun getListFromLocalFile(list: ItemList): ItemList {
        Log.d("1LogD", list.title)
        return coroutineIOScope.async(SupervisorJob()) {
            val listFromFile = list.uri?.let { uri ->
                app.contentResolver.openInputStream(uri).use {
                    gson.fromJson(it?.reader(), ItemList::class.java)
                }
            } ?: list
            listFromFile.apply {
                uri = list.uri
            }
        }.await()
    }


    suspend fun saveListFile(
        backupUri: String?,
        list: ItemList,
        onNewFileCreated: suspend (ItemList, Uri?) -> Unit
    ): ItemList {
        if (backupUri != null) {
            list.uri.let {
                if (it == null
                    || !it.fileExists
                    || !it.isIntoBackupFolder(Uri.parse(backupUri))
                    || !it.canWrite
                ) {
                    val uri = createListFile(backupUri, list)?.uri
                    onNewFileCreated(list, uri)
                }
            }

            list.uri?.let { uri ->
                try {
                    app.contentResolver.openOutputStream(uri, "wt").use { out ->
                        out?.write(
                            gson.toJson(list).toByteArray(Charsets.UTF_8)
                        )
                    }
                } catch (e: Exception) {
                    coroutineIOScope.launch {
                        Toast.makeText(
                            app,
                            app.getString(
                                R.string.error_saving_to_path,
                                list.title
                            ), // todo change to path to just error while saving list
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        return list
    }

    private fun createListFile(backupUri: String, list: ItemList): DocumentFile? {
        val folderUri = Uri.parse(backupUri)
        return Uri.parse(backupUri)?.let {
            return if (DocumentFileCompat.fromUri(app, it)?.canWrite() == true) {
                val folder = DocumentFileCompat.fromUri(app, folderUri)
                folder?.makeFile(app, "${list.title}-${list.id}.1list")
            } else null
        }
    }


    // TODO Toasts should not be here !
    fun deleteListBackupFile(list: ItemList) {
        coroutineIOScope.launch {
            list.uri?.let { uri ->
                try {
                    DocumentFile.fromSingleUri(app, uri)?.delete()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            app,
                            app.getString(R.string.file_deleted),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            app,
                            app.getString(R.string.error_deleting_list_file),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


    suspend fun createListFromUri(
        uri: Uri, onListCreated: suspend (list: ItemList) -> Unit
    ): ItemList {
        return withContext(Dispatchers.IO) {
            try {
                val gson = Gson()
                val content =
                    app.contentResolver.openInputStream(uri)
                        .use { iss -> iss?.bufferedReader()?.use { it.readText() } }
                // return :
                gson.fromJson(content, ItemList::class.java).also {
                    onListCreated(it)
                }
            } catch (e: IllegalArgumentException) {
                throw e
            } catch (e: Exception) {
                throw IOException(app.getString(R.string.error_opening_file))
            }
        }
    }

    suspend fun saveAllListToFiles(
        backupUri: String,
        lists: List<ItemList>,
        onNewFileCreated: (ItemList, Uri?) -> Unit
    ) {
        return withContext(Dispatchers.IO) {
            lists.forEach {
                saveListFile(backupUri, it, onNewFileCreated)
            }
        }
    }

    fun revokeAllAccessFolders() {
        DocumentFileCompat.getAccessibleUris(app)
            .flatMap { it.value }
            .forEach {
                app.contentResolver.releasePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
    }


}