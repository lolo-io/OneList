package com.lolo.io.onelist.core.domain.use_cases

import android.net.Uri
import com.lolo.io.onelist.core.testing.data.createTestList
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import com.lolo.io.onelist.core.testing.fake.FakeSaveListToDb
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RemoveListTest {
    private lateinit var repository: FakeOneListRepository
    private lateinit var removeList : RemoveList

    @Before
    fun setUp() {
        repository = FakeOneListRepository()
        removeList = RemoveList(repository)
    }

    @Test
    fun removeList_noDeleteBackup() = runTest {
        val list = createTestList()
        var callbackCalled = false
        removeList(list, false) {
            callbackCalled = true
        }
        assertContains(repository.calledFunctions, FakeOneListRepository::deleteList.name)
        assertEquals(false, callbackCalled)
    }

    @Test
    fun removeList_deleteBackup() = runTest {
        val list = createTestList().copy(
            uri = Uri.EMPTY
        )
        var callbackCalled = false
        removeList(list, true) {
            callbackCalled = true
        }
        assertContains(repository.calledFunctions, FakeOneListRepository::deleteList.name)
        assertEquals(true, callbackCalled)
    }
}