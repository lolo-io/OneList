package com.lolo.io.onelist.core.data.file_access

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.isTreeDocumentFile
import com.anggrayudi.storage.file.makeFile
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
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

    private fun Uri.isIntoBackupFolder(): Boolean =
        DocumentFileCompat.fromUri(app, this)?.isTreeDocumentFile == true

    private val Uri.fileExists
        get() =
            DocumentFileCompat.fromUri(app, this)?.exists() == true

    @Throws(
        FileNotFoundException::class,
        JsonSyntaxException::class,
        JsonIOException::class,
        SecurityException::class
    )
    suspend fun getListFromLocalFile(list: com.lolo.io.onelist.core.model.ItemList): com.lolo.io.onelist.core.model.ItemList {
        return coroutineIOScope.async(SupervisorJob()) {
            val listFromFile = list.uri?.let { uri ->
                app.contentResolver.openInputStream(uri).use {
                    gson.fromJson(it?.reader(), com.lolo.io.onelist.core.model.ItemList::class.java)
                }
            } ?: list
            listFromFile.apply {
                id = list.id
                uri = list.uri
            }
        }.await()
    }


    suspend fun saveListFile(
        backupUri: String?,
        list: com.lolo.io.onelist.core.model.ItemList,
        onNewFileCreated: suspend (com.lolo.io.onelist.core.model.ItemList, Uri?) -> Unit
    ): com.lolo.io.onelist.core.model.ItemList {
        if (backupUri != null) {
            list.uri.let {
                if (
                    it == null
                    || !it.fileExists
                    || !it.isIntoBackupFolder()
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
                    // Just don't save list in file. error has been toasted before normally.
                    // Should be handled better
                }
            }
        }
        return list
    }

    private fun createListFile(backupUri: String, list: com.lolo.io.onelist.core.model.ItemList): DocumentFile? {
        val folderUri = Uri.parse(backupUri)
        return Uri.parse(backupUri)?.let {
            return if (DocumentFileCompat.fromUri(app, it)?.canWrite() == true) {
                val folder = DocumentFileCompat.fromUri(app, folderUri)
                folder?.makeFile(app, "${list.title}-${list.id}.1list")
            } else null
        }
    }


    @kotlin.jvm.Throws
    fun deleteListBackupFile(
        list: com.lolo.io.onelist.core.model.ItemList,
        onFileDeleted: () -> Unit,
    ) {
        coroutineIOScope.run {
            list.uri?.let { uri ->
                if (
                    DocumentFile.fromSingleUri(app, uri)?.delete()
                    != true
                ) {
                    throw IOException("Could not delete file")
                }
                onFileDeleted()
            }
        }
    }


    suspend fun createListFromUri(
        uri: Uri, onListCreated: suspend (list: com.lolo.io.onelist.core.model.ItemList) -> Unit
    ): com.lolo.io.onelist.core.model.ItemList {
        return withContext(Dispatchers.IO) {
            try {
                val gson = Gson()
                val content =
                    app.contentResolver.openInputStream(uri)
                        .use { iss -> iss?.bufferedReader()?.use { it.readText() } }
                // return :
                gson.fromJson(content, com.lolo.io.onelist.core.model.ItemList::class.java).also {
                    it.uri = uri
                    onListCreated(it)
                }

            } catch (e: IllegalArgumentException) {
                throw e
            } catch (e: Exception) {
                // Todo Change Error to not use R
                //throw IOException(app.getString(R.string.error_opening_file))
                throw IOException("error_opening_file")
            }
        }
    }

    suspend fun saveAllListToFiles(
        backupUri: String,
        lists: List<com.lolo.io.onelist.core.model.ItemList>,
        onNewFileCreated: (com.lolo.io.onelist.core.model.ItemList, Uri?) -> Unit
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