package com.lolo.io.onelist.core.data.repository

import android.net.Uri
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.testing.data.testLists
import com.lolo.io.onelist.core.testing.fake.FakeFileAccess
import com.lolo.io.onelist.core.testing.fake.FakeItemListDao
import com.lolo.io.onelist.core.testing.fake.FakeSharedPreferenceHelper
import com.lolo.io.onelist.core.testing.rules.MainDispatcherRule
import com.lolo.io.onelist.core.testing.util.assertWaiting
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Rule
import org.junit.Test
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull


class OneListRepositoryTest {

    // todo make Repo Android Agnostic
    // to test importList, we must first remove the class Uri from the repo

    @get:Rule
    val dispatcherRule = MainDispatcherRule()


    private val preferences = FakeSharedPreferenceHelper()
    private val dao = FakeItemListDao()
    private val fileAccess = FakeFileAccess()

    private val oneListRepository = OneListRepositoryImpl(
        preferences = preferences,
        dao = dao,
        fileAccess = fileAccess,
    )

    @After
    fun tearDown() {
        fileAccess.tearDown()
        preferences.tearDown()
    }

    @Test
    fun createList() = runTest {
        repeat(3) { i ->
            val listTitle = "Item List $i"
            val createdList = oneListRepository.createList(ItemList(listTitle))

            assert(dao.lists.any { it.title == listTitle })
            oneListRepository.allListsWithErrors.value.lists.any { it.title == listTitle }

            assert(createdList.position == i)
        }
    }

    @Test
    fun getAllLists() = runTest {

        dao.setLists(
            (1..3).map { i ->
                ItemList("Item List $i")
            }
        )
        fileAccess.setShouldThrow(FileNotFoundException())

        val listsWithErrors = oneListRepository.getAllLists()

        assertEquals(3, listsWithErrors.value.lists.size)
        // Should have 0 errors even with fileAccess.setShouldThrow because lists don't have uri
        // & preferences.preferUseFiles == false
        assertEquals(0, listsWithErrors.value.errors.size)
    }

    @Test
    fun getAllLists_preferUseFiles() = runTest {

        dao.setLists(
            (1..3).map { i ->
                ItemList("Item List $i")
            } + ItemList("Item List $4", uri = Uri.EMPTY)
                    + (5..8).map { i ->
                ItemList("Item List $i")
            }
        )

        preferences.backupUri = "URI"
        preferences.preferUseFiles = true

        fileAccess.setShouldThrow(FileNotFoundException())

        val listsWithErrors = oneListRepository.getAllLists()

        assertEquals(dao.lists.size, listsWithErrors.value.lists.size)
        // Should have 1 error because only one list has an uri
        assertEquals(1, listsWithErrors.value.errors.size)
    }


    @Test
    fun saveList() = runTest {
        dao.setLists(
            listOf(ItemList("Item List"))
        )

        val updatedTitle = "Updated Title"

        // update a list in dao
        val updatedList = dao.lists.first().copy(updatedTitle)

        oneListRepository.saveList(updatedList)

        assertEquals(updatedTitle, dao.lists.first().title)
        assertEquals(0, fileAccess.tempSavedFiles.size)
    }

    @Test
    fun saveList_withBackupUri() = runTest {
        dao.setLists(
            (1..3).map { i ->
                ItemList("Item List $i")
            }
        )

        val updatedTitle = "Updated Title"

        // update a list in dao
        val updatedList = dao.lists.first().copy(updatedTitle)

        preferences.backupUri = "URI"

        oneListRepository.saveList(updatedList)

        assertEquals(updatedTitle, dao.lists.first().title)
        assertEquals(1, fileAccess.tempSavedFiles.size)
    }


    @Test
    fun backupAllLists() = runBlocking {
        val lists = (1..3).map { i ->
            ItemList("Item List $i", id = i.toLong())
        }
        oneListRepository.backupLists(lists)

        assertWaiting { lists.size == dao.lists.size }
        assertEquals(0, fileAccess.tempSavedFiles.size)
    }

    @Test
    fun backupAllLists_withBackupUri() = runBlocking {
        val lists = (1..3).map { i ->
            ItemList("Item List $i", id = i.toLong())
        }
        oneListRepository.backupLists(lists)

        preferences.backupUri = "URI"

        assertWaiting { lists.size == dao.lists.size }
        assertEquals(lists.size, fileAccess.tempSavedFiles.size)
    }


    @Test
    fun deleteList() = runBlocking {
        dao.setLists(
            listOf(ItemList("Item List"))
        )
        oneListRepository.deleteList(dao.lists.first())
        assertEquals(0, dao.lists.size)
    }


    @Test
    fun deleteList_withFile() = runBlocking {
        dao.setLists(
            listOf(ItemList("Item List"))
        )

        preferences.backupUri = "URI"

        var fileDeleted = false
        oneListRepository.deleteList(dao.lists.first(), true) {
            fileDeleted = true
        }

        assertEquals(0, dao.lists.size)
        assertWaiting { fileDeleted }
    }

    @Test
    fun deleteList_withError() = runBlocking {
        dao.setLists(
            listOf(ItemList("Item List"))
        )
        preferences.backupUri = "URI"
        fileAccess.setShouldThrow(IOException("Error"))


        assertFailsWith<IOException> {
            oneListRepository.deleteList(dao.lists.first(), true) {
            }
        }

        // Database list should have been updated event with file deletion error
        assertEquals(0, dao.lists.size)
    }

    @Test
    fun selectList() = runTest {
        dao.setLists(testLists)
        oneListRepository.getAllLists()
        assertEquals(testLists.size, oneListRepository.allListsWithErrors.value.lists.size)
        oneListRepository.allListsWithErrors.value.lists.shuffled().forEach {
            oneListRepository.selectList(it)
            assertEquals(testLists.indexOf(it), preferences.selectedListIndex)
        }
    }

    @Test
    fun syncAllLists() = runTest {

        dao.setLists(testLists)
        oneListRepository.getAllLists()

        preferences.backupUri = "FAKE_URI"

        oneListRepository.backupAllListsToFiles()

        assertEquals(testLists.size, fileAccess.tempSavedFiles.size)
    }

    @Test
    fun syncAllLists_noBackupUri() = runTest {
        dao.setLists(testLists)
        oneListRepository.getAllLists()

        oneListRepository.backupAllListsToFiles()

        assertEquals(0, fileAccess.tempSavedFiles.size)
    }

    @Test
    fun setBackupUri_switchToNull() = runTest {
        // todo can test only with Uri = null; need to remove Uri from Repository
        preferences.backupUri = "FAKE_URI"
        oneListRepository.setBackupUri(null, null)
        assertNull(preferences.backupUri)
    }
}