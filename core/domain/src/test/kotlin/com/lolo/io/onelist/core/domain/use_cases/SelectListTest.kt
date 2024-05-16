package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.testing.data.createTestList
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContains

class SelectListTest {
    private lateinit var repository: FakeOneListRepository
    private lateinit var selectList: SelectList

    @Before
    fun setUp() {
        repository = FakeOneListRepository()
        selectList = SelectList(repository)
    }

    @Test
    fun selectListUseCase() = runTest {
        val list = createTestList()
        selectList(list)
        assertContains(repository.calledFunctions, FakeOneListRepository::selectList.name)
    }
}