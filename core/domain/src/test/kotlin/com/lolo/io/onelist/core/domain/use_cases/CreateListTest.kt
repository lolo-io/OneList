package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.testing.data.createTestList
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class CreateListTest {
    private lateinit var repository: FakeOneListRepository
    private lateinit var createList: CreateList

    @Before
    fun setUp() {
        repository = FakeOneListRepository()
        createList = CreateList(repository)
    }

    @Test
    fun clearListUseCase() = runTest {
        val list = createTestList()
        val actual = createList(list)
        val expected = repository.createList(list)
        assertContains(repository.calledFunctions, FakeOneListRepository::createList.name)
        assertEquals(
            expected.title,
            actual.title
        )
        assertEquals(
            expected.items,
            actual.items
        )
    }
}