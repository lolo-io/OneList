package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.testing.data.createTestList
import com.lolo.io.onelist.core.testing.data.testLists
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class SaveListToDbTest {
    private lateinit var repository: FakeOneListRepository
    private lateinit var saveListToDb: SaveListToDb

    @Before
    fun setUp() {
        repository = FakeOneListRepository()
        saveListToDb = SaveListToDbImpl(repository)
    }

    @Test
    fun saveListToDbUseCase() = runTest {
        val list = createTestList()
        val actual = saveListToDb(list)
        val expected = repository.saveList(list)
        assertContains(repository.calledFunctions, FakeOneListRepository::saveList.name)
        assertEquals(expected, actual)
    }
}