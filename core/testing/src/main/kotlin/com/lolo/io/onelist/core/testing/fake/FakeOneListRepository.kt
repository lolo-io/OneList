package com.lolo.io.onelist.core.testing.fake

import android.net.Uri
import com.lolo.io.onelist.core.data.model.ListsWithErrors
import com.lolo.io.onelist.core.data.repository.OneListRepository
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.testing.data.createTestList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeOneListRepository(
    private val preferenceHelper: FakeSharedPreferenceHelper = FakeSharedPreferenceHelper(),
    lists: List<ItemList> = listOf()
) : OneListRepository {

    private var listIdsIncrement = lists.size.toLong()

    val testMutableAllListsWithErrors =
        MutableStateFlow(ListsWithErrors(lists))
    override val allListsWithErrors: StateFlow<ListsWithErrors>
        get() {
            return testMutableAllListsWithErrors.asStateFlow()
        }

    val selectedList
        get() = allListsWithErrors.value.lists[preferenceHelper.selectedListIndex]

    fun setFakeLists(itemLists: List<ItemList>) {
        testMutableAllListsWithErrors.value = ListsWithErrors(itemLists)
    }

    override suspend fun getAllLists(): StateFlow<ListsWithErrors> {
        delay(300)
        return testMutableAllListsWithErrors
    }

    override suspend fun createList(itemList: ItemList): ItemList {
        val newList = itemList.copy(id = ++listIdsIncrement)
        testMutableAllListsWithErrors.value =
            ListsWithErrors(testMutableAllListsWithErrors.value.lists + newList)
        return newList
    }

    override suspend fun saveList(itemList: ItemList) {
        delay(300)
        testMutableAllListsWithErrors.value = ListsWithErrors(testMutableAllListsWithErrors.value
            .lists.map { if (it.id == itemList.id) itemList else it })
    }

    override suspend fun deleteList(
        itemList: ItemList,
        deleteBackupFile: Boolean,
        onFileDeleted: () -> Unit
    ) {
        testMutableAllListsWithErrors.value =
            ListsWithErrors(testMutableAllListsWithErrors.value.lists
                .filter { it.id != itemList.id })


        if (deleteBackupFile) {
            onFileDeleted()
        }
    }

    override suspend fun importList(uri: Uri): ItemList {

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
        preferenceHelper.selectedListIndex =
            testMutableAllListsWithErrors.value.lists.indexOf(list)
    }

    override suspend fun backupLists(lists: List<ItemList>) {
        testMutableAllListsWithErrors.value = ListsWithErrors(lists)
    }

    override suspend fun backupAllListsToFiles() {
        delay(300)
    }

    override suspend fun setBackupUri(uri: Uri?, displayPath: String?) {
        delay(300)
    }
}