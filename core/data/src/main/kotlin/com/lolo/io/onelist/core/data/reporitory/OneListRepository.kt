package com.lolo.io.onelist.core.data.reporitory

import android.net.Uri
import com.lolo.io.onelist.core.data.model.ListsWithErrors
import com.lolo.io.onelist.core.model.ItemList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface OneListRepository {
    val allListsWithErrors: StateFlow<ListsWithErrors>
    suspend fun getAllLists(): Flow<ListsWithErrors>
    suspend fun createList(itemList: ItemList): ItemList
    suspend fun saveListToDb(itemList: ItemList)
    @Throws
    suspend fun deleteList(
        itemList: ItemList,
        deleteBackupFile: Boolean,
        onFileDeleted: () -> Unit
    )
    suspend fun importList(uri: Uri): ItemList
    fun selectList(list: ItemList)
    suspend fun saveAllLists(lists: List<ItemList>)
    suspend fun syncAllLists()
    suspend fun setBackupUri(uri: Uri?, displayPath: String?)
}