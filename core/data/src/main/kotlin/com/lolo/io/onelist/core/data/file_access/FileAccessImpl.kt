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
import com.lolo.io.onelist.core.model.ItemList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException

class FileAccessImpl(
    private val app: Application,
) : FileAccess {
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
    override suspend fun getListFromLocalFile(list: ItemList): ItemList {
        return coroutineIOScope.async(SupervisorJob()) {
            val listFromFile = list.uri?.let { uri ->
                app.contentResolver.openInputStream(uri).use {
                    gson.fromJson(it?.reader(), ItemList::class.java)
                }
            } ?: list
            listFromFile.apply {
                id = list.id
                uri = list.uri
            }
        }.await()
    }


    override suspend fun saveListFile(
        backupUri: String?,
        list: ItemList,
        onNewFileCreated: suspend (ItemList, Uri?) -> Unit
    ): ItemList {

        if (backupUri != null) {
            list.uri.let {
                if (
                    it == null
                    || !it.fileExists
                    || !it.isIntoBackupFolder()
                    || !it.canWrite
                ) {
                    val uri = createListFile(backupUri, list)?.uri
                    uri?.let {
                        writeListsToFile(uri, list)
                        onNewFileCreated(list, uri)
                    }
                }
            }

            list.uri?.let { uri ->
                writeListsToFile(uri, list)
            }
        }
        return list
    }

    private fun writeListsToFile(
        uri: Uri,
        list: ItemList,
    ) {
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

    private fun createListFile(backupUri: String, list: ItemList): DocumentFile? {
        val folderUri = Uri.parse(backupUri)
        return Uri.parse(backupUri)?.let {
            return if (DocumentFileCompat.fromUri(app, it)?.canWrite() == true) {
                val folder = DocumentFileCompat.fromUri(app, folderUri)
                folder?.makeFile(app, "${list.title}-${list.id}.1list")
            } else null
        }
    }


    @kotlin.jvm.Throws
    override fun deleteListBackupFile(
        list: ItemList,
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

    override suspend fun createListFromUri(
        uri: Uri, onListCreated: suspend (list: ItemList) -> Unit
    ): ItemList {
        return withContext(Dispatchers.IO) {
            try {
                val gson = Gson()
                val content =
                    app.contentResolver.openInputStream(uri)
                        .use { iss -> iss?.bufferedReader()?.use { it.readText() } }

                gson.fromJson(content, ItemList::class.java).also {
                    it.uri = uri
                    onListCreated(it)
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override fun revokeAllAccessFolders() {
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