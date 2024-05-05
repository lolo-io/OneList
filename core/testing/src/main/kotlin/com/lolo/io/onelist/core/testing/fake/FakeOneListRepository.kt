package com.lolo.io.onelist.core.testing.fake

import android.net.Uri
import com.lolo.io.onelist.core.data.datamodel.ListsWithErrors
import com.lolo.io.onelist.core.data.repository.OneListRepository
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.testing.data.createTestList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeOneListRepository(
    private val preferenceHelper: SharedPreferencesHelper = FakeSharedPreferenceHelper(),
    lists: List<ItemList> = listOf()
) : OneListRepository {

    val calledFunctions = mutableListOf<String>()

    private var listIdsIncrement = lists.size.toLong()

    val testMutableAllListsWithErrors =
        MutableStateFlow(ListsWithErrors(lists))
    override val allListsWithErrors: StateFlow<ListsWithErrors>
        get() {
            calledFunctions += Thread.currentThread().stackTrace[1].methodName
            return testMutableAllListsWithErrors.asStateFlow()
        }

    val selectedList
        get() = allListsWithErrors.value.lists[preferenceHelper.selectedListIndex]

    fun setFakeLists(itemLists: List<ItemList>) {
        testMutableAllListsWithErrors.value = ListsWithErrors(itemLists)
    }

    override suspend fun getAllLists(): StateFlow<ListsWithErrors> {
        calledFunctions += Thread.currentThread().stackTrace[1].methodName
        delay(300)
        return testMutableAllListsWithErrors
    }

    override suspend fun createList(itemList: ItemList): ItemList {
        calledFunctions += Thread.currentThread().stackTrace[1].methodName
        val newList = itemList.copy(id = ++listIdsIncrement)
        testMutableAllListsWithErrors.value =
            ListsWithErrors(testMutableAllListsWithErrors.value.lists + newList)
        return newList
    }

    override suspend fun saveList(itemList: ItemList) {
        calledFunctions += Thread.currentThread().stackTrace[1].methodName
        delay(300)
        testMutableAllListsWithErrors.value = ListsWithErrors(testMutableAllListsWithErrors.value
            .lists.map { if (it.id == itemList.id) itemList else it })
    }

    override suspend fun deleteList(
        itemList: ItemList,
        deleteBackupFile: Boolean,
        onFileDeleted: () -> Unit
    ) {
        calledFunctions += Thread.currentThread().stackTrace[1].methodName
        testMutableAllListsWithErrors.value =
            ListsWithErrors(testMutableAllListsWithErrors.value.lists
                .filter { it.id != itemList.id })


        if (deleteBackupFile) {
            onFileDeleted()
        }
    }

    override suspend fun importList(uri: Uri): ItemList {
        calledFunctions += Thread.currentThread().stackTrace[1].methodName
        val newList = createTestList(
            position = testMutableAllListsWithErrors.value.lists.size,
            id = listIdsIncrement
        )

        testMutableAllListsWithErrors.value =
            ListsWithErrors(
                testMutableAllListsWithErrors.value.lists + newList
            )

        return newList
    }

    override fun selectList(list: ItemList) {
        calledFunctions += Thread.currentThread().stackTrace[1].methodName
        preferenceHelper.selectedListIndex =
            testMutableAllListsWithErrors.value.lists.indexOf(list)
    }

    override suspend fun backupListsAsync(lists: List<ItemList>) {
        calledFunctions += Thread.currentThread().stackTrace[1].methodName
        testMutableAllListsWithErrors.value = ListsWithErrors(lists)
    }

    override suspend fun backupAllListsToFiles() {
        calledFunctions += Thread.currentThread().stackTrace[1].methodName
        delay(300)
    }

    override suspend fun setBackupUri(uri: Uri?, displayPath: String?) {
        calledFunctions += Thread.currentThread().stackTrace[1].methodName
        delay(300)
    }
}