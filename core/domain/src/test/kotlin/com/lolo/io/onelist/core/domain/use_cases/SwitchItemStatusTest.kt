package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.testing.data.createEmptyTestList
import com.lolo.io.onelist.core.testing.data.testItemCommentDisplayed
import com.lolo.io.onelist.core.testing.data.testItemWithComment
import com.lolo.io.onelist.core.testing.fake.FakeSaveListToDb
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SwitchItemStatusTest {
    private lateinit var saveListToDb : FakeSaveListToDb
    private lateinit var switchItemStatus: SwitchItemStatus

    @Before
    fun setUp() {
        saveListToDb = FakeSaveListToDb()
        switchItemStatus = SwitchItemStatus(saveListToDb)
    }

    @Test
    fun switchItemStatusTestUseCase() = runTest {
        val list = createEmptyTestList().apply {
            items += listOf(testItemWithComment)
        }
        val actual = switchItemStatus(list, list.items[0])
        assertTrue(saveListToDb.hasBeenCalled)
        assertEquals(!testItemWithComment.done, actual.items[0].done)
    }
}
