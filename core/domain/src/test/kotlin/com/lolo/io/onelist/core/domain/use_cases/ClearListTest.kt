package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.domain.use_cases.ClearList
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.testing.data.createTestList
import com.lolo.io.onelist.core.testing.fake.FakeSaveListToDb
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ClearListTest {

    private lateinit var saveListToDb : FakeSaveListToDb
    private lateinit var clearList : ClearList

    @Before
    fun setUp() {
        saveListToDb = FakeSaveListToDb()
        clearList = ClearList(saveListToDb)
    }

    @Test
    fun clearListUseCase() = runTest {
        val list = createTestList()
        val actual = clearList(list)
        assertTrue(saveListToDb.hasBeenCalled)
        assertEquals(emptyList(), actual.items)
    }
}