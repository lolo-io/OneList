package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.testing.data.createTestList
import com.lolo.io.onelist.core.testing.fake.FakeSaveListToDb
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EditItemOfListTest {
    private lateinit var saveListToDb : FakeSaveListToDb
    private lateinit var editItemOfList : EditItemOfList

    @Before
    fun setUp() {
        saveListToDb = FakeSaveListToDb()
        editItemOfList = EditItemOfList(saveListToDb)
    }

    @Test
    fun editItemOfListUseCase() = runTest {
        val list = createTestList()
        val item = list.items[0].copy("Edited", "Edited")
        val actual = editItemOfList(list, item)
        assertTrue (saveListToDb.hasBeenCalled)
        assertEquals(item, actual.items[0])
    }
}