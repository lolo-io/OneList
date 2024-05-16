package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.testing.data.createTestList
import com.lolo.io.onelist.core.testing.data.testLists
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import com.lolo.io.onelist.core.testing.fake.FakeSharedPreferenceHelper
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HandleFirstLaunchTest {
    private lateinit var repository: FakeOneListRepository
    private lateinit var preferences: FakeSharedPreferenceHelper
    private lateinit var handleFirstLaunch: HandleFirstLaunch

    @Before
    fun setUp() {
        repository = FakeOneListRepository()
        preferences = FakeSharedPreferenceHelper()
        handleFirstLaunch = HandleFirstLaunch(repository, preferences)
    }

    @Test
    fun handleFirstLaunchUseCase_firstLaunch() = runTest {
        val lists = testLists

        preferences.firstLaunch = true

        val actual = handleFirstLaunch(lists)

        assertContains(repository.calledFunctions, FakeOneListRepository::createList.name)
        assertEquals(lists.size, repository.calledFunctions.filter {
            it == FakeOneListRepository::createList.name
        }.size)

        assertEquals(false, actual)
    }

    @Test
    fun handleFirstLaunchUseCase_notFirstLaunch() = runTest {
        val lists = testLists

        preferences.firstLaunch = false

        val actual = handleFirstLaunch(lists)

        assertEquals(0, repository.calledFunctions.filter {
            it == FakeOneListRepository::createList.name
        }.size)

        assertEquals(false, actual)
    }
}