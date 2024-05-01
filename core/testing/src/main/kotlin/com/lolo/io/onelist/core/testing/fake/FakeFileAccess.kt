package com.lolo.io.onelist.core.testing.fake

import android.net.Uri
import com.lolo.io.onelist.core.data.file_access.FileAccess
import com.lolo.io.onelist.core.data.model.ErrorLoadingList
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.model.preview
import com.lolo.io.onelist.core.testing.data.createTestList
import kotlinx.coroutines.delay
import kotlin.random.Random

class FakeFileAccess : FileAccess {

    private var exceptionThrown : Throwable? = null
    fun setShouldThrow(exception: Throwable) {
        exceptionThrown = exception
    }

    val tempSavedFiles = mutableListOf<ItemList>()
    private var testUriList = createTestUriList()

    fun tearDown() {
        exceptionThrown = null
        tempSavedFiles.clear()
        testUriList = createTestUriList()
    }
    private fun createTestUriList() = ItemList(
        "Local File List",
        items = (0..5).map { itemIndex ->
            Item.preview.copy(
                title = "Test Item $itemIndex",
                id = 100L + itemIndex,
                comment = if (Random.nextBoolean()) "Test Item $itemIndex Comment" else ""
            )
        },
    )

    override suspend fun getListFromLocalFile(list: ItemList): ItemList {
        exceptionThrown ?.let { throw it }
        return testUriList.copy(uri = Uri.EMPTY)
    }

    override suspend fun saveListFile(
        backupUri: String?,
        list: ItemList,
        onNewFileCreated: suspend (ItemList, Uri?) -> Unit
    ): ItemList {
        delay(300)
        onNewFileCreated(list, Uri.EMPTY)
        tempSavedFiles.add(list)
        return list
    }

    override fun deleteListBackupFile(list: ItemList, onFileDeleted: () -> Unit) {
        exceptionThrown ?.let { throw it }
        onFileDeleted()
    }

    override suspend fun createListFromUri(
        uri: Uri,
        onListCreated: suspend (list: ItemList) -> Unit
    ): ItemList {
        delay(300)
        val list = createTestList()
        onListCreated(list)
        return list
    }

    override fun revokeAllAccessFolders() {

    }
}