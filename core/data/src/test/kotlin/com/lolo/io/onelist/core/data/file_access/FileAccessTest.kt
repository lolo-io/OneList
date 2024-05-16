package com.lolo.io.onelist.core.data.file_access

import android.app.Application
import androidx.core.net.toFile
import com.lolo.io.onelist.core.data.utils.EXISTING_TEST_LIST_FILE_NAME
import com.lolo.io.onelist.core.data.utils.existingListFileUri
import com.lolo.io.onelist.core.data.utils.testFilesFolderPathUri
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.testing.data.createTestList
import com.lolo.io.onelist.core.testing.util.assertWaiting
import com.lolo.io.onelist.core.testing.util.suspendWithActivity
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FileAccessTest {

    @After
    fun tearDown() {
        testFilesFolderPathUri.toFile().listFiles()?.forEach {
            if (it.nameWithoutExtension != EXISTING_TEST_LIST_FILE_NAME) {
                it.deleteRecursively()
            }
        }
    }

    @Test
    fun getListFromLocalFile() {

        val itemListToGet = ItemList(
            uri = existingListFileUri
        )

        val expectedItemList = createTestList()

        suspendWithActivity {
            val fileAccess = FileAccessImpl(
                this.application.applicationContext as Application
            )

            val list = fileAccess.getListFromLocalFile(itemListToGet)

            assertEquals(expectedItemList.title, list.title)
            assertEquals(expectedItemList.items, list.items)
        }
    }

    @Test
    fun saveListFile() {
        suspendWithActivity {
            val fileAccess = FileAccessImpl(
                this.application.applicationContext as Application
            )

            var methodCalled = false
            val itemListToSave = createTestList()
            fileAccess.saveListFile(
                backupUri = testFilesFolderPathUri.toString(),
                list = itemListToSave,
                onNewFileCreated = { list, uri ->
                    methodCalled = true
                    assertNotNull(uri)
                    uri?.let {
                        val file = uri.toFile()
                        assertTrue(file.exists())
                        assertEquals(
                            "${list.title}-${list.id}",
                            file.nameWithoutExtension
                        )
                        assert(file.length() > 0)
                    }
                }
            )

            assertWaiting { methodCalled }
        }
    }

    @Test
    fun createListFromUri() {

        val expectedItemList = createTestList()

        suspendWithActivity {
            val fileAccess = FileAccessImpl(
                this.application.applicationContext as Application
            )

            var methodCalled = false
            val list = fileAccess.createListFromUri(existingListFileUri,
                onListCreated = { list ->
                    methodCalled = true
                    assertEquals(expectedItemList.title, list.title)
                    assertEquals(expectedItemList.items, list.items)
                })

            assertEquals(expectedItemList.title, list.title)
            assertEquals(expectedItemList.items, list.items)
            assertWaiting { methodCalled }

        }

    }
}