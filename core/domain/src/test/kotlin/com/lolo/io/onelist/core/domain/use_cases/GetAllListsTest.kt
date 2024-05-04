package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.testing.data.createTestList
import com.lolo.io.onelist.core.testing.data.testLists
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import com.lolo.io.onelist.core.testing.fake.FakeSaveListToDb
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class GetAllListsTest {
    private lateinit var repository: FakeOneListRepository
    private lateinit var getAllLists : GetAllLists

    @Before
    fun setUp() {
        repository = FakeOneListRepository()
        getAllLists = GetAllLists(repository)
    }

    @Test
    fun getAllListsUseCase() = runTest {
        val lists = testLists
        repository.setFakeLists(lists)
        val actual = getAllLists()
        assertContains(repository.calledFunctions.map { it.lowercase() },
            "get${FakeOneListRepository::allListsWithErrors.name}".lowercase())
        assertEquals(
            repository.allListsWithErrors.value,
            actual.value)
    }
}