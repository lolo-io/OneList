package com.lolo.io.onelist.core.testing.fake

import android.net.Uri
import com.lolo.io.onelist.core.data.model.ListsWithErrors
import com.lolo.io.onelist.core.data.reporitory.OneListRepository
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.testing.data.createTestList
import com.lolo.io.onelist.core.testing.data.testLists
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

val fakeOneListRepository = FakeOneListRepository()
class FakeOneListRepository(lists: List<ItemList> = testLists) : OneListRepository {

    private var selectedListIndex = 0

    private var listIdsIncrement = lists.size.toLong()

    val testMutableAllListsWithErrors =
        MutableStateFlow(ListsWithErrors(lists))
    override val allListsWithErrors: StateFlow<ListsWithErrors>
        get() = testMutableAllListsWithErrors.asStateFlow()

    override suspend fun getAllLists(): Flow<ListsWithErrors> {
        delay(300)
        return testMutableAllListsWithErrors
    }

    override suspend fun createList(itemList: ItemList) {
        testMutableAllListsWithErrors.value =
            ListsWithErrors(testMutableAllListsWithErrors.value.lists + itemList.copy(id = ++listIdsIncrement))
    }

    override suspend fun saveListToDb(itemList: ItemList) {
        delay(300)
    }

    override suspend fun deleteList(
        itemList: ItemList,
        deleteBackupFile: Boolean,
        onFileDeleted: () -> Unit
    ) {
        testMutableAllListsWithErrors.value =
            ListsWithErrors(testMutableAllListsWithErrors.value.lists
                .filter { it.id != itemList.id })

        val position = testMutableAllListsWithErrors.value.lists.indexOf(itemList)

        selectedListIndex = if (testMutableAllListsWithErrors.value.lists.isNotEmpty()) {
            val nextSelected = testMutableAllListsWithErrors.value.lists.getOrNull(position)
                ?: testMutableAllListsWithErrors.value.lists[position - 1]
            testMutableAllListsWithErrors.value.lists.indexOf(nextSelected)
        } else {
            0
        }

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

    override fun selectList(list: ItemList) { }

    override suspend fun saveAllLists(lists: List<ItemList>) {
        testMutableAllListsWithErrors.value = ListsWithErrors(lists)
    }

    override suspend fun syncAllLists() {
        delay(300)
    }

    override suspend fun setBackupUri(uri: Uri?, displayPath: String?) {
        delay(300)
    }
}