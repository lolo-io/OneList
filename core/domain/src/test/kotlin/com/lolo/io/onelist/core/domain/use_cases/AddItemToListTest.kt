package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.testing.data.createTestList
import com.lolo.io.onelist.core.testing.data.testItemWithComment
import com.lolo.io.onelist.core.testing.fake.FakeSaveListToDb
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AddItemToListTest {
    private lateinit var saveListToDb : FakeSaveListToDb
    private lateinit var addItemToList : AddItemToList

    @Before
    fun setUp() {
        saveListToDb = FakeSaveListToDb()
        addItemToList = AddItemToList(saveListToDb)
    }

    @Test
    fun addItemToListUseCase() = runTest {
        val list = createTestList()
        val item = testItemWithComment
        val actual = addItemToList(list, item)
        assertTrue(saveListToDb.hasBeenCalled)
        assert(actual.items.indexOf(item) > -1)
    }
}