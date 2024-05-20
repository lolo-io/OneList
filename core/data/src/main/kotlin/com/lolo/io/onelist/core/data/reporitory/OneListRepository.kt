package com.lolo.io.onelist.core.data.reporitory

import android.net.Uri
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.lolo.io.onelist.core.data.file_access.FileAccess
import com.lolo.io.onelist.core.data.model.ListsWithErrors
import com.lolo.io.onelist.core.data.model.ErrorLoadingList
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import com.lolo.io.onelist.core.data.utils.toItemListEntity
import com.lolo.io.onelist.core.data.utils.updateOne
import com.lolo.io.onelist.core.database.util.toItemListModel
import com.lolo.io.onelist.core.database.util.toItemListModels
import com.lolo.io.onelist.core.model.ItemList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException


class OneListRepository(
    private val preferences: SharedPreferencesHelper,
    private val dao: com.lolo.io.onelist.core.database.dao.ItemListDao,
    private val fileAccess: FileAccess
) {

    private val _allListsWithErrors =
        MutableStateFlow(ListsWithErrors())

    val allListsWithErrors
        get() = _allListsWithErrors.asStateFlow()

    suspend fun getAllLists(): Flow<ListsWithErrors> {
        withContext(Dispatchers.IO) {
            val allListsFromDb = dao.getAll()
            val errors = mutableListOf<ErrorLoadingList>()
            val lists = if (
                preferences.preferUseFiles &&
                preferences.backupUri != null
            ) {
                allListsFromDb.map {
                    val list = it.toItemListModel()
                    try {
                        supervisorScope {
                            fileAccess.getListFromLocalFile(list)
                        }
                    } catch (e: SecurityException) {
                        errors += ErrorLoadingList.PermissionDeniedError
                        list
                    } catch (e: FileNotFoundException) {
                        errors += ErrorLoadingList.FileMissingError
                        list
                    } catch (e: IllegalArgumentException) {
                        errors += ErrorLoadingList.FileMissingError
                        print(e)
                        list
                    } catch (e: JsonSyntaxException) {
                        errors += ErrorLoadingList.FileCorruptedError
                        list
                    } catch (e: JsonIOException) {
                        errors += ErrorLoadingList.FileCorruptedError
                        list
                    } catch (e: Exception) {
                        errors += ErrorLoadingList.UnknownError
                        list
                    }
                }
            } else {
                allListsFromDb.toItemListModels()
            }

            _allListsWithErrors.value = ListsWithErrors(
                lists = lists,
                errors = errors.distinct(),
            )
        }

        return _allListsWithErrors
    }

    suspend fun createList(itemList: ItemList) {
        itemList.position = _allListsWithErrors.value.lists.size - 1
        _allListsWithErrors.value =
            ListsWithErrors(_allListsWithErrors.value.lists + upsertList(itemList))
        preferences.selectedListIndex = _allListsWithErrors.value.lists.size - 1
    }


    // does upsert in dao, and if has backup uri -> save list file; can create a file
    // and also update alllists flow
    suspend fun saveListToDb(itemList: ItemList) {
        upsertList(itemList).let {
            _allListsWithErrors.value = ListsWithErrors(
                _allListsWithErrors.value.lists.updateOne(itemList) { it.id == itemList.id })
        }
    }


    // does upsert in dao, and if has backup uri -> save list file; can create a file
    private suspend fun upsertList(list: ItemList): ItemList {

        val upsertInDao: suspend (list: ItemList) -> Unit = { insertList ->
            withContext(Dispatchers.IO) {
                dao.upsert(insertList.toItemListEntity()).takeIf { it > 0 }?.let {
                    insertList.id = it
                }
            }
        }

        return withContext(Dispatchers.IO) {
            upsertInDao(list)
            if (preferences.backupUri != null) {
                fileAccess.saveListFile(
                    preferences.backupUri,
                    list,
                    onNewFileCreated = { list, uri ->
                        list.uri = uri
                        upsertInDao(list)
                        _allListsWithErrors.value = ListsWithErrors(
                            _allListsWithErrors.value.lists.updateOne(list) { it.id == list.id })
                    })
            } else list
        }
    }


    @Throws
    suspend fun deleteList(
        itemList: ItemList,
        deleteBackupFile: Boolean,
        onFileDeleted: () -> Unit
    ) {

        withContext(Dispatchers.IO) {
            dao.delete(itemList.toItemListEntity())
        }

        val position = _allListsWithErrors.value.lists.indexOf(itemList)

        _allListsWithErrors.value =
            ListsWithErrors(_allListsWithErrors.value.lists
                .filter { it.id != itemList.id })

        if(_allListsWithErrors.value.lists.isNotEmpty()) {
            val nextSelected = _allListsWithErrors.value.lists.getOrNull(position)
                ?: _allListsWithErrors.value.lists[position - 1]
            selectList(nextSelected)
        } else {
            preferences.selectedListIndex = 0
        }


        if (deleteBackupFile) {
            fileAccess.deleteListBackupFile(itemList, onFileDeleted)
        }
    }

    suspend fun importList(uri: Uri): ItemList {
        val list = fileAccess.createListFromUri(uri,
            onListCreated = {
                saveListToDb(
                    it.copy(
                        id = 0L,
                        position = _allListsWithErrors.value.lists.size
                    )
                )
            })
        getAllLists()
        preferences.selectedListIndex = _allListsWithErrors.value.lists.size - 1
        return list
    }

    fun selectList(list: ItemList) {
        preferences.selectedListIndex = _allListsWithErrors.value.lists.indexOf(list)
    }

    suspend fun saveAllLists(lists: List<ItemList>) {
        _allListsWithErrors.value = ListsWithErrors(lists)
        coroutineScope {
            // update async to improve list move performance.
            CoroutineScope(Dispatchers.IO).launch {
                lists.forEach {
                    upsertList(it)
                }
            }
        }

    }

    suspend fun syncAllLists() {
        preferences.backupUri?.let {
            _allListsWithErrors.value.lists.forEach {
                upsertList(it)
            }
        }
    }

    suspend fun setBackupUri(uri: Uri?, displayPath: String?) {
        if (uri != null) {
            preferences.backupUri = uri.toString()
            preferences.backupDisplayPath = displayPath
            syncAllLists()
        } else {
            preferences.backupUri = null
            preferences.backupDisplayPath = null
            fileAccess.revokeAllAccessFolders()
        }
    }
}