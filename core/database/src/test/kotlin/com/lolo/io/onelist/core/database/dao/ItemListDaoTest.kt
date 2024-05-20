package com.lolo.io.onelist.core.database.dao

import androidx.room.Room
import com.lolo.io.onelist.core.database.OneListDatabase
import com.lolo.io.onelist.core.testing.data.testListsEntities
import com.lolo.io.onelist.core.testing.util.withActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ItemListDaoTest {

    private lateinit var itemListDao: ItemListDao
    private lateinit var db: OneListDatabase

    @Before
    fun createDb() = withActivity {
        db = Room.inMemoryDatabaseBuilder(
            this,
            OneListDatabase::class.java,
        ).build()
        itemListDao = db.itemListDao
    }

    @Test
    fun getAll() = runTest {

        val itemLists = testListsEntities

        val actual = withContext(Dispatchers.IO) {
            itemListDao.upsertMany(itemLists)
            itemListDao.getAll()
        }

        assertEquals(itemLists.size, actual.size)

        itemLists.forEachIndexed { i, it ->
            assertEquals(it.title, actual[i].title)
            assertEquals(it.position, actual[i].position)
            assert(actual[i].id != 0L)
            it.items.forEachIndexed { j, expectedItem ->
                val actualItem = actual[i].items[j]
                assertEquals(expectedItem.title, actualItem.title)
                assertEquals(expectedItem.comment, actualItem.comment)
                assertEquals(expectedItem.done, actualItem.done)
                assertEquals(expectedItem.commentDisplayed, actualItem.commentDisplayed)
                assert(actualItem.id != 0L)
            }
        }
    }

    @Test
    fun get() = runTest {

        val itemList = testListsEntities[0]

        val actual = withContext(Dispatchers.IO) {
            val insertedId = itemListDao.upsert(itemList)
            itemListDao.get(insertedId)
        }

        assertEquals(itemList.title, actual.title)
        assertEquals(itemList.position, actual.position)
        assert(actual.id != 0L)
        itemList.items.forEachIndexed { j, expectedItem ->
            val actualItem = actual.items[j]
            assertEquals(expectedItem.title, actualItem.title)
            assertEquals(expectedItem.comment, actualItem.comment)
            assertEquals(expectedItem.done, actualItem.done)
            assertEquals(expectedItem.commentDisplayed, actualItem.commentDisplayed)
            assert(actualItem.id != 0L)
        }
    }

    @Test
    fun delete() = runTest {
        val itemList = testListsEntities[0]

        val insertedId = withContext(Dispatchers.IO) {
            itemListDao.upsert(itemList)
        }

        withContext(Dispatchers.IO) {
            itemListDao.delete(itemList)
        }

        val list = withContext(Dispatchers.IO) {
            itemListDao.get(insertedId)
        }

        assertNull(list)
    }
}