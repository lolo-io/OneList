package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.testing.data.testLists
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class LoadAllListsTest {
    private lateinit var repository: FakeOneListRepository
    private lateinit var loadAllLists: LoadAllLists

    @Before
    fun setUp() {
        repository = FakeOneListRepository()
        loadAllLists = LoadAllLists(repository)
    }

    @Test
    fun loadAllListsTestUseCase() = runTest {
        val lists = testLists
        repository.setFakeLists(lists)
        val actual = loadAllLists()
        val expected = repository.getAllLists()
        assertContains(repository.calledFunctions, FakeOneListRepository::getAllLists.name)
        assertEquals(expected, actual)
    }
}