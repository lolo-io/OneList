package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.testing.data.createEmptyTestList
import com.lolo.io.onelist.core.testing.data.createTestList
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import com.lolo.io.onelist.core.testing.fake.FakeSaveListToDb
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class SetItemsOfListTest {
    private lateinit var saveListToDb: FakeSaveListToDb
    private lateinit var setItemOfList: SetItemsOfList

    @Before
    fun setUp() {
        saveListToDb = FakeSaveListToDb()
        setItemOfList = SetItemsOfList(saveListToDb)
    }

    @Test
    fun setItemOfListUseCase() = runTest {
        val list = createEmptyTestList()
        val items = createTestList().items
        val actual = setItemOfList(list, items)
        assertEquals(true, saveListToDb.hasBeenCalled)
        assertEquals(items, actual.items)
    }
}