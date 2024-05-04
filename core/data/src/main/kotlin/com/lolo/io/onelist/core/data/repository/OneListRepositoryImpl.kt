package com.lolo.io.onelist.core.data.repository

import android.net.Uri
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.lolo.io.onelist.core.data.file_access.FileAccess
import com.lolo.io.onelist.core.data.datamodel.ListsWithErrors
import com.lolo.io.onelist.core.data.datamodel.ErrorLoadingList
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import com.lolo.io.onelist.core.data.utils.toItemListEntity
import com.lolo.io.onelist.core.data.utils.updateOneIf
import com.lolo.io.onelist.core.database.dao.ItemListDao
import com.lolo.io.onelist.core.database.util.toItemListModel
import com.lolo.io.onelist.core.database.util.toItemListModels
import com.lolo.io.onelist.core.model.ItemList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException

class OneListRepositoryImpl(
    private val preferences: SharedPreferencesHelper,
    private val dao: ItemListDao,
    private val fileAccess: FileAccess
) : OneListRepository {

    private val _allListsWithErrors =
        MutableStateFlow(ListsWithErrors())

    override val allListsWithErrors
        get() = _allListsWithErrors.asStateFlow()

    override suspend fun getAllLists(): StateFlow<ListsWithErrors> {
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

    override suspend fun createList(itemList: ItemList): ItemList {
        val addedList = upsertList(itemList)
        itemList.position = _allListsWithErrors.value.lists.size
        _allListsWithErrors.value =
            ListsWithErrors(_allListsWithErrors.value.lists + addedList)

        return addedList
    }


    // does upsert in dao, and if has backup uri -> save list file; can create a file
    // and also update allLists flow
    override suspend fun saveList(itemList: ItemList) {
        upsertList(itemList).let {
            _allListsWithErrors.value = ListsWithErrors(
                _allListsWithErrors.value.lists.updateOneIf(itemList) { it.id == itemList.id })
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
                            _allListsWithErrors.value.lists.updateOneIf(list) { it.id == list.id })
                    })
            } else list
        }
    }


    @Throws
    override suspend fun deleteList(
        itemList: ItemList,
        deleteBackupFile: Boolean,
        onFileDeleted: () -> Unit
    ) {

        withContext(Dispatchers.IO) {
            dao.delete(itemList.toItemListEntity())
        }

        _allListsWithErrors.value =
            ListsWithErrors(_allListsWithErrors.value.lists
                .filter { it.id != itemList.id })


        if (deleteBackupFile) {
            fileAccess.deleteListBackupFile(itemList, onFileDeleted)
        }
    }

    override suspend fun importList(uri: Uri): ItemList {
        val list = fileAccess.createListFromUri(uri,
            onListCreated = {
                saveList(
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

    override fun selectList(list: ItemList) {
        preferences.selectedListIndex = _allListsWithErrors.value.lists.indexOf(list)
    }

    override suspend fun backupAllListsToFiles() {
        preferences.backupUri?.let {
            _allListsWithErrors.value.lists.forEach {
                upsertList(it)
            }
        }
    }


    override suspend fun backupListsAsync(lists: List<ItemList>) {
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

    override suspend fun setBackupUri(uri: Uri?, displayPath: String?) {
        if (uri != null) {
            preferences.backupUri = uri.toString()
            preferences.backupDisplayPath = displayPath
            backupAllListsToFiles()
        } else {
            preferences.backupUri = null
            preferences.backupDisplayPath = null
            fileAccess.revokeAllAccessFolders()
        }
    }
}