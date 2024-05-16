package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.testing.data.createTestList
import com.lolo.io.onelist.core.testing.fake.FakeSaveListToDb
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RemoveItemFromListTest {
    private lateinit var saveListToDb : FakeSaveListToDb
    private lateinit var removeItemFromList : RemoveItemFromList

    @Before
    fun setUp() {
        saveListToDb = FakeSaveListToDb()
        removeItemFromList = RemoveItemFromList(saveListToDb)
    }

    @Test
    fun removeItemFromListUseCase() = runTest {
        val list = createTestList()
        val item = list.items[0]
        val actual = removeItemFromList(list, item)
        assertTrue(saveListToDb.hasBeenCalled)
        assertEquals(list.items - item, actual.items)
    }
}