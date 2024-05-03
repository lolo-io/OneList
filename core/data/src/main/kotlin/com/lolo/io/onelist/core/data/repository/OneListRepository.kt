package com.lolo.io.onelist.core.data.repository

import android.net.Uri
import com.lolo.io.onelist.core.data.datamodel.ListsWithErrors
import com.lolo.io.onelist.core.model.ItemList
import kotlinx.coroutines.flow.StateFlow

interface OneListRepository {
    val allListsWithErrors: StateFlow<ListsWithErrors>
    suspend fun getAllLists(): StateFlow<ListsWithErrors>
    suspend fun createList(itemList: ItemList): ItemList
    suspend fun saveList(itemList: ItemList)
    @Throws
    suspend fun deleteList(
        itemList: ItemList,
        deleteBackupFile: Boolean = false,
        onFileDeleted: () -> Unit = {}
    )
    suspend fun importList(uri: Uri): ItemList
    fun selectList(list: ItemList)
    suspend fun backupLists(lists: List<ItemList>)
    suspend fun backupAllListsToFiles()
    suspend fun setBackupUri(uri: Uri?, displayPath: String?)
}