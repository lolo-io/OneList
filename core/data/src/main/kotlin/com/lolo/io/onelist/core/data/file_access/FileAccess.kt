package com.lolo.io.onelist.core.data.file_access

import android.net.Uri
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.lolo.io.onelist.core.model.ItemList
import java.io.FileNotFoundException

interface FileAccess {
    @Throws(
        FileNotFoundException::class,
        JsonSyntaxException::class,
        JsonIOException::class,
        SecurityException::class
    )
    suspend fun getListFromLocalFile(list: ItemList): ItemList

    suspend fun saveListFile(
        backupUri: String?,
        list: ItemList,
        onNewFileCreated: suspend (ItemList, Uri?) -> Unit
    ): ItemList

    @kotlin.jvm.Throws
    fun deleteListBackupFile(
        list: ItemList,
        onFileDeleted: () -> Unit,
    )

    suspend fun createListFromUri(
        uri: Uri, onListCreated: suspend (list: ItemList) -> Unit
    ): ItemList

    fun revokeAllAccessFolders()
}