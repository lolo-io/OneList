package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.testing.data.createEmptyTestList
import com.lolo.io.onelist.core.testing.data.createTestList
import com.lolo.io.onelist.core.testing.data.testItemCommentDisplayed
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import com.lolo.io.onelist.core.testing.fake.FakeSaveListToDb
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SwitchItemCommentShownTest {
    private lateinit var saveListToDb : FakeSaveListToDb
    private lateinit var switchItemCommentShown: SwitchItemCommentShown

    @Before
    fun setUp() {
        saveListToDb = FakeSaveListToDb()
        switchItemCommentShown = SwitchItemCommentShown(saveListToDb)
    }

    @Test
    fun switchItemCommentShownUseCase() = runTest {
        val list = createEmptyTestList().apply {
            items += listOf(testItemCommentDisplayed)
        }
        val actual = switchItemCommentShown(list, list.items[0])
        assertTrue(saveListToDb.hasBeenCalled)
        assertEquals(!testItemCommentDisplayed.commentDisplayed, actual.items[0].commentDisplayed)
    }
}