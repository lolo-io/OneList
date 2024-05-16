package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.testing.data.testLists
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ReorderListsTest {
    private lateinit var repository: FakeOneListRepository
    private lateinit var reorderLists : ReorderLists

    @Before
    fun setUp() {
        repository = FakeOneListRepository()
        reorderLists = ReorderLists(repository)
    }

    @Test
    fun reorderListsUseCase() = runTest {
        val lists = testLists
        val actual = reorderLists(lists, lists[0])
        assertContains(repository.calledFunctions, FakeOneListRepository::backupListsAsync.name)
        assertContains(repository.calledFunctions, FakeOneListRepository::selectList.name)
        actual.forEachIndexed { index, itemList ->
            assertEquals(index + 1, itemList.position)
        }
    }
}